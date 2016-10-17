package com.virohtus.dht.core.transport.server;

import com.virohtus.dht.core.transport.connection.Connection;

public interface ServerDelegate {
    void connectionOpened(Connection connection);
    void serverShutdown();
}
