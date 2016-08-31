package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.event.ErrorEvent;
import com.virohtus.dht.event.EventProtocol;

import java.io.IOException;

public class ConnectionError extends ErrorEvent {

    public ConnectionError(byte[] data) throws IOException {
        super(data);
    }

    public ConnectionError(Exception e) {
        super(e);
    }

    @Override
    public int getType() {
        return EventProtocol.CONNECTION_ERROR;
    }

}
