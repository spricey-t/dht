package com.virohtus.dht.connection.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

public class ConnectionDetailsRequest extends Event {
    @Override
    public int getType() {
        return EventProtocol.CONNECTION_DETAILS_REQUEST;
    }
}
