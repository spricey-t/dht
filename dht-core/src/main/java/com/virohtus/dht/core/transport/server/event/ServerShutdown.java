package com.virohtus.dht.core.transport.server.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;

public class ServerShutdown extends Event {

    public ServerShutdown() {
    }

    public ServerShutdown(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.SERVER_SHUTDOWN;
    }
}
