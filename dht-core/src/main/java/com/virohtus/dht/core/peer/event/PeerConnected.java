package com.virohtus.dht.core.peer.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.peer.Peer;

import java.io.IOException;

public class PeerConnected extends Event {

    private Peer peer;

    public PeerConnected(Peer peer) {
        this.peer = peer;
    }

    public PeerConnected(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.PEER_CONNECTED;
    }

    public Peer getPeer() {
        return peer;
    }
}
