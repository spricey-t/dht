package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.overlay.Finger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GetPredecessorResponse extends Event {

    private Finger predecessor;

    public GetPredecessorResponse(String initiatingNodeId, Finger predecessor) {
        super(initiatingNodeId);
        this.predecessor = predecessor;
    }

    public GetPredecessorResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.GET_PREDECESSOR_RESPONSE;
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        dhtUtilities.writeSizedData(predecessor.serialize(), dataOutputStream);
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        predecessor = new Finger(dhtUtilities.readSizedData(dataInputStream));
    }

    public Finger getPredecessor() {
        return predecessor;
    }
}
