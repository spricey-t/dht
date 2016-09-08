package com.virohtus.dht.transport.server;

import com.virohtus.dht.DedicatedTask;
import com.virohtus.dht.evt.EventHandler;

/**
 * Defines how a dht server is controlled and where to delegate
 * server events. Implementation must be threadsafe
 */
public interface Server extends DedicatedTask {
    void setEventHandler(EventHandler eventHandler);
    int getPort();
}
