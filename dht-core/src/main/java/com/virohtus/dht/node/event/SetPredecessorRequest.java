package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

import java.io.IOException;

public class SetPredecessorRequest extends Event {

    public SetPredecessorRequest(String initiatingNodeId) {
        super(initiatingNodeId);
    }

    public SetPredecessorRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.SET_PREDECESSOR_REQUEST;
    }
}
