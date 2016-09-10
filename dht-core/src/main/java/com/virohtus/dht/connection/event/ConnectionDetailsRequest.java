package com.virohtus.dht.connection.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

import java.io.IOException;

public class ConnectionDetailsRequest extends Event {

    public ConnectionDetailsRequest() {
    }

    public ConnectionDetailsRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.CONNECTION_DETAILS_REQUEST;
    }
}
