package com.pf8x.exception;

public class Pf8xIllegalStateException extends RuntimeException {
    public Pf8xIllegalStateException() {
    }

    public Pf8xIllegalStateException(String message) {
        super(message);
    }

    public Pf8xIllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public Pf8xIllegalStateException(Throwable cause) {
        super(cause);
    }

    public Pf8xIllegalStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
