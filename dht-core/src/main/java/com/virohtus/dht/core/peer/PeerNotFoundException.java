package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.network.NodeIdentity;

public class PeerNotFoundException extends Exception {

    public PeerNotFoundException(NodeIdentity nodeIdentity) {
        super("could not find peer with node identity: " + nodeIdentity);
    }

    public PeerNotFoundException(String peerId) {
        super("could not find peer with id: " + peerId);
    }
}
