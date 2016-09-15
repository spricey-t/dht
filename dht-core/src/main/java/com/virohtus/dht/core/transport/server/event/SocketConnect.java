package com.virohtus.dht.core.transport.server.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;
import java.net.Socket;

public class SocketConnect extends Event {

    private Socket socket;

    public SocketConnect(Socket socket) {
        this.socket = socket;
    }

    public SocketConnect(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.SOCKET_CONNECT;
    }

    public Socket getSocket() {
        return socket;
    }
}
