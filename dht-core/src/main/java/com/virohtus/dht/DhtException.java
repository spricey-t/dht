package com.virohtus.dht;

public class DhtException extends Exception {
    public DhtException() {
    }

    public DhtException(String message) {
        super(message);
    }

    public DhtException(String message, Throwable cause) {
        super(message, cause);
    }

    public DhtException(Throwable cause) {
        super(cause);
    }

    public DhtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
