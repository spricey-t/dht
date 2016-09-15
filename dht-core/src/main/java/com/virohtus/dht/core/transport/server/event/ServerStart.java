package com.virohtus.dht.core.transport.server.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;

public class ServerStart extends Event {

    private int port;

    public ServerStart(int port) {
        this.port = port;
    }

    public ServerStart(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.SERVER_START;
    }

    public int getPort() {
        return port;
    }
}
