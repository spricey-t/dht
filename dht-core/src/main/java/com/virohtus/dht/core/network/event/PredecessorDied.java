package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class PredecessorDied extends Event {

    private NodeIdentity initiator;

    public PredecessorDied(NodeIdentity initiator) {
        this.initiator = initiator;
    }

    public PredecessorDied(byte[] data) throws IOException {
        super(data);
    }

    public NodeIdentity getInitiator() {
        return initiator;
    }

    @Override
    public int getType() {
        return DhtProtocol.PREDECESSOR_DIED;
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeSizedData(initiator.getBytes());
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        initiator = new NodeIdentity(dhtInputStream.readSizedData());
    }
}
