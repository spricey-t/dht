package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.event.ErrorEvent;
import com.virohtus.dht.event.EventProtocol;

public class ConnectionError extends ErrorEvent {

    public ConnectionError(Exception e) {
        super(e);
    }

    @Override
    public int getType() {
        return EventProtocol.CONNECTION_ERROR;
    }

}
