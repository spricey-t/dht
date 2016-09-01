package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.overlay.transport.ConnectionError;

import java.io.IOException;

public class ReceiveError extends ConnectionError {

    public ReceiveError(byte[] data) throws IOException {
        super(data);
    }

    public ReceiveError(Exception e) {
        super(e);
    }

    @Override
    public int getType() {
        return EventProtocol.RECEIVER_ERROR;
    }


}
