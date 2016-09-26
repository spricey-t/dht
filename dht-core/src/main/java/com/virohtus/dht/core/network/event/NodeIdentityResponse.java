package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.ResponseEvent;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class NodeIdentityResponse extends ResponseEvent {

    private NodeIdentity nodeIdentity;

    public NodeIdentityResponse(String requestId, NodeIdentity nodeIdentity) {
        super(requestId);
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

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeSizedData(nodeIdentity.getBytes());
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        nodeIdentity = new NodeIdentity(dhtInputStream.readSizedData());
    }
}
