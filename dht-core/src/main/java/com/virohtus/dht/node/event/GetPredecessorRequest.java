package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

import java.io.IOException;

public class GetPredecessorRequest extends Event {

    public GetPredecessorRequest(String initiatingNodeId) {
        super(initiatingNodeId);
    }

    public GetPredecessorRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.GET_PREDECESSOR_REQUEST;
    }
}
