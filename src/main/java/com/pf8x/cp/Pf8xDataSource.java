package com.pf8x.cp;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class Pf8xDataSource implements DataSource {
    private final ConnectionPool connectionPool;

    public Pf8xDataSource(Pf8xDataSourceConfig config) throws SQLException {
        connectionPool = new BlockingConnectionPool(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.connection();
    }

    /**
     * this will close the data source gracefully and cleanup all resources with exception handling.
     */
    public void closeGracefully() {
        connectionPool.closeGracefully();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("this operation is not supported. please submit a issue if there is a good use case.");
    }
}
