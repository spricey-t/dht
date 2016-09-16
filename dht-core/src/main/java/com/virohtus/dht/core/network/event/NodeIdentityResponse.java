package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.NodeIdentity;

import java.io.IOException;

public class NodeIdentityResponse extends Event {

    private NodeIdentity nodeIdentity;

    public NodeIdentityResponse(NodeIdentity nodeIdentity) {
        this.nodeIdentity = nodeIdentity;
    }

    public NodeIdentityResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.NODE_IDENTITY_RESPONSE;
    }

    public NodeIdentity getNodeIdentity() {
        return nodeIdentity;
    }
}
