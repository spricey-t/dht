package com.virohtus.dht.node;

public class PeerNotFoundException extends Exception {

    private final String peerId;

    public PeerNotFoundException(String peerId) {
        super("Could not find peer with id: " + peerId);
        this.peerId = peerId;
    }

    public String getPeerId() {
        return peerId;
    }
}
