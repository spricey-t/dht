package com.virohtus.dht.core.transport.server;

import com.virohtus.dht.core.transport.connection.ConnectionInfo;

import java.io.IOException;

public interface Server {
    void start(int port) throws IOException;
    void shutdown();
    boolean isAlive();
    ConnectionInfo getConnectionInfo();
}
