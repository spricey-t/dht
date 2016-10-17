package com.virohtus.dht.core.engine.action.peer;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.network.peer.Peer;

public class PeerDisconnected extends Action {

    private final Peer peer;

    public PeerDisconnected(Peer peer) {
        this.peer = peer;
    }

    public Peer getPeer() {
        return peer;
    }
}
