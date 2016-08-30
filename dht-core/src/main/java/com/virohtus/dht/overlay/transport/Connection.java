package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ConnectionDelegate;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public abstract class Connection {

    private final String id;
    protected final ConnectionDelegate delegate;
    protected final Socket socket;

    public Connection(ConnectionDelegate delegate, Socket socket) {
        this.id = UUID.randomUUID().toString();
        this.delegate = delegate;
        this.socket = socket;
    }

    public String getId() {
        return id;
    }

    public abstract void send(byte[] data) throws IOException;
    public abstract void close();
}
