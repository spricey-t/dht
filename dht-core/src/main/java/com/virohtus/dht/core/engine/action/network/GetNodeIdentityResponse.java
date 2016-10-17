package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.ResponseAction;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class GetNodeIdentityResponse extends ResponseAction {

    private NodeIdentity nodeIdentity;

    public GetNodeIdentityResponse(String requestId, NodeIdentity nodeIdentity) {
        super(requestId);
        this.nodeIdentity = nodeIdentity;
    }

    public GetNodeIdentityResponse(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    public NodeIdentity getNodeIdentity() {
        return nodeIdentity;
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_NODE_IDENTITY_RESPONSE;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        super.toWire(outputStream);
        nodeIdentity.toWire(outputStream);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        super.fromWire(inputStream);
        nodeIdentity = new NodeIdentity(inputStream);
    }
}
