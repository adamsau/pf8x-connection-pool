package com.pf8x.cp;

import com.pf8x.connection.Pf8xConnection;
import com.pf8x.exception.Pf8xIllegalStateException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ConnectionPoolTest {
    private BlockingConnectionPool cp;
    private final int MAX_SIZE = 10;
    private final int PENDING_CONNECTION_SOFT_LIMIT = 64;
    private final String URL = "jdbc:postgresql://127.0.0.1:5432/bench?user=bench&password=123456";
    private DataSource DATASOURCE;
    private final int PRIORITY_THRESHOLD = 16;

    @Before
    public void setup() throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(URL);
        DATASOURCE = ds;
        Pf8xDataSourceConfig config = new Pf8xDataSourceConfig.Pf8xDataSourceConfigBuilder()
                .maxSize(MAX_SIZE).pendingConnectionSoftLimit(PENDING_CONNECTION_SOFT_LIMIT).dataSource(DATASOURCE)
                .autoCommit(false).priorityThreshold(PRIORITY_THRESHOLD).build();
        cp = new BlockingConnectionPool(config);
    }

    @After
    public void tearDown() {
        cp.closeGracefully();
    }

    @Test
    public void shouldGetConnection() throws SQLException{
        Connection con = cp.connection();
        assertNotNull(con);
    }

    @Test
    public void shouldGetLastUsedConnection() throws SQLException {
        Connection con = cp.connection();
        cp.recycle((Pf8xConnection) con);
        Connection con2 = cp.connection();
        assertEquals(con, con2);
    }

    @Test
    public void shouldRecycle() throws Exception{
        Connection con = null;
        for(int i = 0; i < MAX_SIZE; ++i) {
            con = cp.connection();
        }
        assertNotNull(con);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        AtomicBoolean ok = new AtomicBoolean(false);
        executorService.submit(() -> {
            try {
                Connection con2 = cp.connection();
                if(con2 != null) ok.set(true);
            }
            catch (SQLException se) {
                throw new RuntimeException(se);
            }
        });
        Thread.sleep(2 * 1000);

        cp.recycle((Pf8xConnection) con);

        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);

        assertEquals(true, ok.get());
    }

    @Test
    public void shouldCloseGracefully() throws Exception{
        List<Connection> connections = new ArrayList<>();
        for(int i = 0; i < MAX_SIZE; ++i) connections.add(cp.connection());
        new Thread(() -> {
            try {
                Connection con = cp.connection();
                System.out.println("just make sure the pending works." + con.toString());
            }
            catch (SQLException se) {
                se.printStackTrace();
            }
        }).start();
        Thread.sleep(2 * 1000);

        cp.closeGracefully();

        for(Connection con: connections) {
            assertEquals(true, con.isClosed());
        }
    }


    @Test
    public void shouldCloseGracefullyWhenRunning() throws Exception {
        //pid to debug thread dump using jstack
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());

        List<Connection> connections = new ArrayList<>();
        for(int i = 0; i < MAX_SIZE; ++i) {
            connections.add(cp.connection());
        }
        for(int i = 0; i < MAX_SIZE; ++i) {
            cp.recycle((Pf8xConnection) connections.get(i));
        }

        int THREAD_COUNT = 64;
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch cdl = new CountDownLatch(THREAD_COUNT);
        CountDownLatch cdl2 = new CountDownLatch(THREAD_COUNT);
        AtomicInteger finish = new AtomicInteger(0);

        for(int t = 0; t < THREAD_COUNT; ++t) {
            executorService.submit(() -> {
                cdl.countDown();
                try {
                    cdl.await();
                }
                catch (InterruptedException ie) {
                    fail();
                }
                try {
                    while (true) {
                        Connection con = null;
                        try {
                            con = null;
                            con = cp.connection();
                            Statement stmt = con.createStatement();
                            stmt.executeQuery("select id from test");
                            stmt.close();
                        } finally {
                            if(con != null) con.close();
                        }

                    }
                }
                catch (SQLException se) {
                    se.printStackTrace();
                    finish.incrementAndGet();
                    cdl2.countDown();
                    try {
                        cdl2.await();
                    }
                    catch (InterruptedException ie2) {
                        fail();
                    }
                }
                catch (Pf8xIllegalStateException ie) {
                    System.out.println("should not happen.");
                    ie.printStackTrace();
                    fail();
                }
            });
        }

        Thread.sleep(2 * 1000);

        cp.closeGracefully();

        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.SECONDS);

        assertEquals(THREAD_COUNT, finish.get());

        for(int i = 0; i < MAX_SIZE; ++i) {
            Connection con = connections.get(i);
            assertEquals(true, con.isClosed());
        }
    }

    /**
     * tricky to test multi-thread and race issues since there are too many combinations.
     * this load test act as a general test on multi-thread.
     * make sure the code assert expected result and throw IllegalStateException if invalid so that this test will fail.
     */
    @Test
    public void shouldNotHaveRaceIssueUnderLotsOfThread() throws Exception {
        //pid to debug thread dump using jstack
        System.out.println(ManagementFactory.getRuntimeMXBean().getName());
        int THREAD_COUNT = 64;
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch cdl = new CountDownLatch(THREAD_COUNT);
        CountDownLatch cdl2 = new CountDownLatch(THREAD_COUNT);
        AtomicInteger finish = new AtomicInteger(0);

        long t1 = System.nanoTime();
        for(int t = 0; t < THREAD_COUNT; ++t) {
            executorService.submit(() -> {
                cdl.countDown();
                try {
                    cdl.await();
                }
                catch (InterruptedException ie) {
                    fail();
                }
                for (int i = 0; i < 5000; ++i) {
                    try {
                        Connection con = cp.connection();
                        Statement stmt = con.createStatement();
                        stmt.executeQuery("select id from test");
                        stmt.close();
                        con.close();
                    } catch (Exception e) {
                        long t3 = System.nanoTime();
                        System.out.println(String.format("time elapsed before exception in ms: %.6f", (t3 - t1) / 1000000.0));
                        e.printStackTrace();
                        fail();
                    }
                }
                finish.incrementAndGet();
                cdl2.countDown();
                try {
                    cdl2.await();
                }
                catch (InterruptedException ie) {
                    fail();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long t2 = System.nanoTime();
        System.out.println(String.format("rough time elapsed in ms: %.6f", (t2 - t1) / 1000000.0));

        assertEquals(THREAD_COUNT, finish.get());
    }
}
