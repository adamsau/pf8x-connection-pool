package com.pf8x.cp;

import javax.sql.DataSource;

public class Pf8xDataSourceConfig {
    private int maxSize = 10;
    private int pendingConnectionSoftLimit = -1;
    private DataSource dataSource;
    private boolean autoCommit;
    private int priorityThreshold = 16;

    private Pf8xDataSourceConfig() {
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getPendingConnectionSoftLimit() {
        return pendingConnectionSoftLimit;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public int getPriorityThreshold() {
        return priorityThreshold;
    }

    public static class Pf8xDataSourceConfigBuilder {
        private Pf8xDataSourceConfig pf8xDataSourceConfig = new Pf8xDataSourceConfig();

        public Pf8xDataSourceConfig build() {
            return pf8xDataSourceConfig;
        }

        /**
         * max size for number of connection in the pool. the pool is a fixed size pool.
         * this should be as small and tight as possible
         * default 10
         * @param maxSize
         * @return
         */
        public Pf8xDataSourceConfigBuilder maxSize(int maxSize) {
            pf8xDataSourceConfig.maxSize = maxSize;
            return this;
        }

        /**
         * a soft limit on number of pending connection, when this limit is reached, exception may thrown to throttle
         * -1 indicates no limit
         * this should be set with respect to total number of threads
         * default -1
         * @param pendingConnectionSoftLimit
         * @return
         */
        public Pf8xDataSourceConfigBuilder pendingConnectionSoftLimit(int pendingConnectionSoftLimit) {
            pf8xDataSourceConfig.pendingConnectionSoftLimit = pendingConnectionSoftLimit;
            return this;
        }

        public Pf8xDataSourceConfigBuilder dataSource(DataSource dataSource) {
            pf8xDataSourceConfig.dataSource = dataSource;
            return this;
        }

        public Pf8xDataSourceConfigBuilder autoCommit(boolean autoCommit) {
            pf8xDataSourceConfig.autoCommit = autoCommit;
            return this;
        }

        /**
         * when the pending connection tries to get a connection but fails, the priority count for this request will increase by 1.
         * when it reaches this threshold, it becomes a priority and the next idle connection will be assign to it immediately.
         * default 16
         * @param priorityThreshold
         * @return
         */
        public Pf8xDataSourceConfigBuilder priorityThreshold(int priorityThreshold) {
            pf8xDataSourceConfig.priorityThreshold = priorityThreshold;
            return this;
        }
    }
}
