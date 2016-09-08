package com.virohtus.dht.transport.connection;

import com.virohtus.dht.DedicatedTask;
import com.virohtus.dht.evt.EventHandler;

/**
 * Represents an open connection to another node. All connections are trusted
 * in this environment. This interface defines how to send events and where to
 * delegate events received. The implementation must be threadsafe.
 */
public interface Connection extends DedicatedTask {
    int getId();
    ConnectionInfo getConnectionInfo();
    void setEventHandler(EventHandler eventHandler);
    void send() throws SendFailedException;
    boolean isAlive();
}
