package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ConnectionDelegate;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public abstract class Connection {

    private final String id;
    private final ConnectionType connectionType;
    protected final ConnectionDelegate delegate;
    protected final Socket socket;

    public Connection(ConnectionType connectionType, ConnectionDelegate delegate, Socket socket) {
        this.id = UUID.randomUUID().toString();
        this.connectionType = connectionType;
        this.delegate = delegate;
        this.socket = socket;
    }

    public String getId() {
        return id;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public abstract void send(byte[] data) throws IOException;
    public abstract void close();
}
