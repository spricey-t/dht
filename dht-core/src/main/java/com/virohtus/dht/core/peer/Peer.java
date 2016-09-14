package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;

public class Peer {

    private String peerId;
    private final ConnectionInfo connectionInfo;
    private Connection connection;

    public Peer(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public String getPeerId() {
        return peerId;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void send(Event event) {
    }

    public boolean isConnected() {
        return false;
    }

}
