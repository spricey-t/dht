package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class GetPredecessorResponse extends Event {

    private NodeIdentity predecessor;

    public GetPredecessorResponse(NodeIdentity predecessor) {
        this.predecessor = predecessor;
    }

    public GetPredecessorResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_PREDECESSOR_RESPONSE;
    }

    public NodeIdentity getPredecessor() {
        return predecessor;
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeBoolean(predecessor != null);
        if(predecessor != null) {
            dhtOutputStream.writeSizedData(predecessor.getBytes());
        }
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        boolean hasPredecessor = dhtInputStream.readBoolean();
        if(hasPredecessor) {
            predecessor = new NodeIdentity(dhtInputStream.readSizedData());
        }
    }
}
