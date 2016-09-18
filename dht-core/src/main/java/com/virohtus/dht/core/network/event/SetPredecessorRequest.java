package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class SetPredecessorRequest extends Event {

    private NodeIdentity nodeIdentity;

    public SetPredecessorRequest(NodeIdentity nodeIdentity) {
        this.nodeIdentity = nodeIdentity;
    }

    public SetPredecessorRequest(byte[] data) throws IOException {
        super(data);
    }

    public NodeIdentity getNodeIdentity() {
        return nodeIdentity;
    }

    @Override
    public int getType() {
        return DhtProtocol.SET_PREDECESSOR_REQUEST;
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
