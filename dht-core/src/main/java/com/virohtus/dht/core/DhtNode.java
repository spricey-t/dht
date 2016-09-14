package com.virohtus.dht.core;

import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.network.FingerTable;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;

public interface DhtNode {
    void start(int serverPort);
    void shutdown();
    void joinNetwork(ConnectionInfo existingNode);
    void leaveNetwork();
    void registerEventHandler(EventHandler handler);
    void unregisterEventHandler(EventHandler handler);
    String getNodeId();
    ConnectionInfo getConnectionInfo();
    FingerTable getFingerTable();
}
