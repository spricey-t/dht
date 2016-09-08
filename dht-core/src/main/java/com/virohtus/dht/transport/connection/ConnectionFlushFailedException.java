package com.virohtus.dht.transport.connection;

import com.virohtus.dht.DhtException;

/**
 * Raised when Connection.close() fails to flush
 */
public class ConnectionFlushFailedException extends DhtException {
    public ConnectionFlushFailedException(String message) {
        super(message);
    }
}
