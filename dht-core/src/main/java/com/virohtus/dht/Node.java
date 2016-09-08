package com.virohtus.dht;

import com.virohtus.dht.evt.Event;
import com.virohtus.dht.evt.EventHandler;
import com.virohtus.dht.transport.connection.ConnectionInfo;

public interface Node extends DedicatedTask {
    void registerEventHandler(EventHandler eventHandler);
    void send(int connectionId, Event event);
    void connect(ConnectionInfo connectionInfo);
}
