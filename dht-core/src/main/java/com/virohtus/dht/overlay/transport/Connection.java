package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ConnectionDelegate;

import java.io.IOException;
import java.net.Socket;

public abstract class Connection {

    protected final ConnectionDelegate delegate;
    protected final Socket socket;

    public Connection(ConnectionDelegate delegate, Socket socket) {
        this.delegate = delegate;
        this.socket = socket;
    }

    public abstract void send(byte[] data) throws IOException;
    public abstract void close();
}
