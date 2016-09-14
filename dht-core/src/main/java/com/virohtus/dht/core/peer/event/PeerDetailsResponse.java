package com.virohtus.dht.core.peer.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.peer.PeerDetails;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class PeerDetailsResponse extends Event {

    private PeerDetails peerDetails;

    public PeerDetailsResponse(PeerDetails peerDetails) {
        this.peerDetails = peerDetails;
    }

    public PeerDetailsResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.PEER_DETAILS_RESPONSE;
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeSizedData(peerDetails.getBytes());
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        peerDetails = new PeerDetails(dhtInputStream.readSizedData());
    }

    public PeerDetails getPeerDetails() {
        return peerDetails;
    }
}
