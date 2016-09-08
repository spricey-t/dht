package com.virohtus.dht.node;

import com.virohtus.dht.DedicatedTask;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventHandler;
import com.virohtus.dht.transport.connection.ConnectionInfo;
import com.virohtus.dht.transport.connection.SendFailedException;

public interface Node extends DedicatedTask {
    void registerEventHandler(EventHandler eventHandler);
    void send(int connectionId, Event event) throws SendFailedException;
    void connect(ConnectionInfo connectionInfo);
    ConnectionInfo getConnectionInfo();
}
