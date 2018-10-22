package com.pf8x.exception;

import java.sql.SQLException;

public class Pf8xSQLException extends SQLException {
    public Pf8xSQLException(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
    }

    public Pf8xSQLException(String reason, String SQLState) {
        super(reason, SQLState);
    }

    public Pf8xSQLException(String reason) {
        super(reason);
    }

    public Pf8xSQLException() {
    }

    public Pf8xSQLException(Throwable cause) {
        super(cause);
    }

    public Pf8xSQLException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public Pf8xSQLException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public Pf8xSQLException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
