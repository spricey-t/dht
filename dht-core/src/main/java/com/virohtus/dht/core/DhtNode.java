package com.virohtus.dht.core;

import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;

import java.io.IOException;

public interface DhtNode {
    void start() throws IOException;
    void shutdown();
    boolean isAlive();
    void joinNetwork(ConnectionInfo existingNode) throws IOException;
    Peer openConnection(ConnectionInfo connectionInfo) throws IOException;
    void leaveNetwork();
}
