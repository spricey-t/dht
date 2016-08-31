package com.virohtus.dht.overlay.transport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {

    private final Map<String, Connection> connections = new HashMap<>();


    public void add(Connection connection) {
        synchronized (connections) {
            connections.put(connection.getId(), connection);
        }
    }
    public Connection remove(String connectionId) {
        synchronized (connections) {
            return connections.remove(connectionId);
        }
    }
    public Set<Connection> clear() {
        synchronized (connections) {
            Set<Connection> connectionSet = new HashSet<>(connections.values());
            connections.clear();
            return connectionSet;
        }
    }

    public Connection get(String connectionId) {
        synchronized (connections) {
            return connections.get(connectionId);
        }
    }

    public Set<Connection> list() {
        synchronized (connections) {
            return new HashSet<>(connections.values());
        }
    }
}
