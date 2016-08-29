package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.event.ErrorEvent;
import com.virohtus.dht.event.EventProtocol;

public class ReceiveError extends ErrorEvent {

    public ReceiveError(Exception e) {
        super(e);
    }

    @Override
    public int getType() {
        return EventProtocol.RECEIVER_ERROR;
    }
}
