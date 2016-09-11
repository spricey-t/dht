package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

import java.io.IOException;

public class PeerDetailsRequest extends Event {

    public PeerDetailsRequest(String initiatingNodeId) {
        super(initiatingNodeId);
    }

    public PeerDetailsRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.PEER_DETAILS_REQUEST;
    }
}
