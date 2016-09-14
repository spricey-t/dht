package com.virohtus.dht.core.peer;

public class PeerNotFoundException extends Exception {

    private final String peerId;

    public PeerNotFoundException(String peerId) {
        super("could not find peer with id: " + peerId);
        this.peerId = peerId;
    }

    public String getPeerId() {
        return peerId;
    }
}
