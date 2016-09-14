package com.virohtus.dht.core.transport.server;

import com.virohtus.dht.core.transport.connection.ConnectionInfo;

public interface Server {
    void start(int port);
    void shutdown();
    boolean isAlive();
    ConnectionInfo getConnectionInfo();
}
