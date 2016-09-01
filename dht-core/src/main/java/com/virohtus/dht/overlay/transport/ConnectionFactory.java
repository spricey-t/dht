package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ConnectionDelegate;
import com.virohtus.dht.overlay.transport.tcp.TCPConnection;

import java.io.IOException;
import java.net.Socket;

public class ConnectionFactory {

    private static final ConnectionFactory instance = new ConnectionFactory();

    private ConnectionFactory() {}

    public static ConnectionFactory getInstance() {
        return instance;
    }

    public TCPConnection createTCPConnection(ConnectionType connectionType, ConnectionDelegate delegate, Socket socket) throws IOException {
        return new TCPConnection(connectionType, delegate, socket);
    }
}
