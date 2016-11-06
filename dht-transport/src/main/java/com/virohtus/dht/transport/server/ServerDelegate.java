package com.virohtus.dht.transport.server;

import com.virohtus.dht.transport.connection.Connection;

public interface ServerDelegate {
    void connectionOpened(Connection connection);
    void serverShutdown();
}
