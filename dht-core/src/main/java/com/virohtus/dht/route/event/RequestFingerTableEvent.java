package com.virohtus.dht.route.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

import java.io.IOException;

public class RequestFingerTableEvent extends Event {

    public RequestFingerTableEvent() {
    }

    public RequestFingerTableEvent(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.REQUEST_FINGER_TABLE_EVENT;
    }

}
