package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

import java.io.IOException;

public class FingerTableRequest extends Event {

    public FingerTableRequest(String initiatingNodeId) {
        super(initiatingNodeId);
    }

    public FingerTableRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.FINGER_TABLE_REQUEST;
    }
}
