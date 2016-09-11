package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.PeerDetails;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PeerDetailsResponse extends Event {

    private PeerDetails peerDetails;

    public PeerDetailsResponse(String initiatingNodeId, PeerDetails peerDetails) {
        super(initiatingNodeId);
        this.peerDetails = peerDetails;
    }

    public PeerDetailsResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.PEER_DETAILS_RESPONSE;
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        dhtUtilities.writeSizedData(peerDetails.serialize(), dataOutputStream);
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        peerDetails = new PeerDetails(dhtUtilities.readSizedData(dataInputStream));
    }

    public PeerDetails getPeerDetails() {
        return peerDetails;
    }
}
