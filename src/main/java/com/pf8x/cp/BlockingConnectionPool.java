package com.pf8x.cp;

import com.google.common.reflect.AbstractInvocationHandler;
import com.pf8x.collection.DoublyLinkedListQueue;
import com.pf8x.collection.LinkedListQueue;
import com.pf8x.collection.SimpleQueue;
import com.pf8x.connection.Pf8xConnection;
import com.pf8x.exception.Pf8xIllegalStateException;
import com.pf8x.exception.Pf8xSQLException;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a blocking connection pool which uses CountDownLatch to block pending connections
 */
public class BlockingConnectionPool extends ConnectionPool {
    /**
     * optimization: favors synchronised queue because pool size is small with frequent enqueue and frequent dequeue
     */
    private final Pf8xConnection[] connectionsRef;
    private final DoublyLinkedListQueue<Pf8xConnection> connectionQueue = new DoublyLinkedListQueue<>(MAX_SIZE);
    private final SimpleQueue<PendingConnectionWrapper> pendingConnectionWrapperQueue = new LinkedListQueue<>(PENDING_CONNECTION_SOFT_LIMIT);
    private final SimpleQueue<PendingConnectionWrapper> priorityPendingConnectionWrapperQueue = new LinkedListQueue<>(PENDING_CONNECTION_SOFT_LIMIT);
    private final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    private AtomicBoolean isShutdown = new AtomicBoolean(false);
    private ThreadLocal<Pf8xConnection> lastConnectionOfThisThread = new ThreadLocal<>();
    private final Logger LOGGER = Logger.getLogger(BlockingConnectionPool.class.getName());

    public BlockingConnectionPool(Pf8xDataSourceConfig config) throws SQLException {
        super(config);

        connectionsRef = new Pf8xConnection[MAX_SIZE];
        for(int i = 0; i < MAX_SIZE; ++i) {
            Connection con = DATASOURCE.getConnection();
            con.setAutoCommit(AUTO_COMMIT);
            Pf8xConnection pf8xConnection = (Pf8xConnection) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {Pf8xConnection.class}, new Pf8xConnectionInvocationHandler(con));
            connectionsRef[i] = pf8xConnection;
            connectionQueue.put(pf8xConnection);
            connectionQueue.enqueue(pf8xConnection);
        }
    }

    @Override
    public Connection connection() throws SQLException {
        if(isShutdown.get()) throw new Pf8xSQLException("connection pool is shutting down. this operation is disabled.");

        /**
         * optimization: favors getting previously used connection because there maybe some caching implementation inside connection
         */
        Pf8xConnection con = connectionQueue.removeOrDequeue(lastConnectionOfThisThread.get());
        lastConnectionOfThisThread.set(con);

        int priority = 0;
        while (con == null) {
            PendingConnectionWrapper pcw = new PendingConnectionWrapper(++priority);
            if(pcw.getPriority() < PRIORITY_THRESHOLD) pendingConnectionWrapperQueue.enqueue(pcw);
            else priorityPendingConnectionWrapperQueue.enqueue(pcw);
            try {
                if(isShutdown.get()) throw new Pf8xSQLException("connection pool is shutting down. this operation is disabled.");
                pcw.await();
            } catch (InterruptedException ie) {
                throw new SQLException(ie);
            }

            if(pcw.getPriority() < PRIORITY_THRESHOLD) con = connectionQueue.dequeue();
            else {
                con = (Pf8xConnection) pcw.getConnection();
                if(!isShutdown.get() && con == null) throw new Pf8xIllegalStateException("assert connection!");
            }
            lastConnectionOfThisThread.set(con);
        }

        return con;
    }

    @Override
    public void recycle(Pf8xConnection con) throws SQLException {
        if(isShutdown.get()) throw new Pf8xSQLException("connection pool is shutting down. this operation is disabled.");

        PendingConnectionWrapper pPcw = priorityPendingConnectionWrapperQueue.dequeue();
        if(pPcw != null) {
            pPcw.setConnection(con);
            pPcw.countDown();
            return;
        }
        /**
         * optimization: favors recycle to pool instead of pending connections,
         * so that new request can get connection immediately, if no new request, this pending request will get the connection.
         */
        connectionQueue.enqueue(con);
        PendingConnectionWrapper pcw = pendingConnectionWrapperQueue.dequeue();
        if (pcw != null) pcw.countDown();
    }

    @Override
    public void closeGracefully() {
        if(!isShutdown.compareAndSet(false, true)) return;

        //abort all connections
        for(int i = 0; i < MAX_SIZE; ++i) {
            try {
                if (connectionsRef[i] != null) connectionsRef[i].abort(executorService);
            }
            catch (SQLException se) {
                LOGGER.log(Level.INFO, "closeGracefully.abortingConnection", se);
            }
        }

        //release all pending connections
        PendingConnectionWrapper pcw = pendingConnectionWrapperQueue.dequeue();
        while(pcw != null) {
            pcw.countDown();
            pcw = pendingConnectionWrapperQueue.dequeue();
        }

        //shutdown executor
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException ie) {
            LOGGER.log(Level.INFO, "closeGracefully.awaitExecutorServiceTermination", ie);
        }
    }

    private final class PendingConnectionWrapper {
        private Connection connection;
        private int priority;

        public PendingConnectionWrapper(int priority) {
            this.priority = priority;
        }

        private CountDownLatch cdl = new CountDownLatch(1);

        void await() throws InterruptedException {
            cdl.await();
        }

        void countDown() {
            cdl.countDown();
        }

        public int getPriority() {
            return priority;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }
    }

    private class Pf8xConnectionInvocationHandler extends AbstractInvocationHandler {
        private Connection connection;

        private final String CLOSE_METHOD = "close";

        public Pf8xConnectionInvocationHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals(CLOSE_METHOD) && args.length == 0) {
                recycle((Pf8xConnection) proxy);
            }
            else {
                return method.invoke(connection, args);
            }
            return null;
        }
    }
}
