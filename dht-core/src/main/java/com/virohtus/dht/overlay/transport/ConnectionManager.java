package com.virohtus.dht.overlay.transport;

public interface ConnectionManager {
    void add(Connection connection);
    Connection remove(String connectionId);
}
