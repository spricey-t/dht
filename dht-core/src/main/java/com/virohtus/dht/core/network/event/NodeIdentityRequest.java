package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;

public class NodeIdentityRequest extends Event {

    public NodeIdentityRequest() {
    }

    public NodeIdentityRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.NODE_IDENTITY_REQUEST;
    }
}
