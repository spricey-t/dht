package com.virohtus.dht.transport.connection;

import com.virohtus.dht.DhtException;

/**
 * Raised when an attempt to Connection.send() fails
 */
public class SendFailedException extends DhtException {
    public SendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
