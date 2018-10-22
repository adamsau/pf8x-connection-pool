package com.pf8x.cp;

import com.pf8x.connection.Pf8xConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionPool {
    protected final int MAX_SIZE;
    protected final int PENDING_CONNECTION_SOFT_LIMIT;
    protected final DataSource DATASOURCE;
    protected final boolean AUTO_COMMIT;
    protected final int PRIORITY_THRESHOLD;

    public ConnectionPool(Pf8xDataSourceConfig config) {
        this.MAX_SIZE = config.getMaxSize();
        this.PENDING_CONNECTION_SOFT_LIMIT = config.getPendingConnectionSoftLimit();
        DATASOURCE = config.getDataSource();
        AUTO_COMMIT = config.isAutoCommit();
        PRIORITY_THRESHOLD = config.getPriorityThreshold();
    }

    abstract public Connection connection() throws SQLException;
    abstract public void recycle(Pf8xConnection con) throws SQLException;
    abstract public void closeGracefully();
}
