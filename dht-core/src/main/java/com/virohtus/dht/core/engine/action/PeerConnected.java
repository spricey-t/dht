package com.virohtus.dht.core.engine.action;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.peer.Peer;

public class PeerConnected extends Action {

    private final Peer peer;

    public PeerConnected(Peer peer ) {
        this.peer = peer;
    }

    public Peer getPeer() {
        return peer;
    }
}
