package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.TransportException;

public class ConnectionException extends TransportException {
    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}
