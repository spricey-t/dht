package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class GetNodeIdentityResponse extends TransportableAction {

    private NodeIdentity nodeIdentity;

    public GetNodeIdentityResponse(NodeIdentity nodeIdentity) {
        this.nodeIdentity = nodeIdentity;
    }

    public GetNodeIdentityResponse(byte[] data) throws IOException {
        super(data);
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
