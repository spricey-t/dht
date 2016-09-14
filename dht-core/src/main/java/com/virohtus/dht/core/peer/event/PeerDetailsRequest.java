package com.virohtus.dht.core.peer.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;

public class PeerDetailsRequest extends Event {

    public PeerDetailsRequest() {
    }

    public PeerDetailsRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.PEER_DETAILS_REQUEST;
    }

}
