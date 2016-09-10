package com.virohtus.dht.node;

import java.net.Socket;

public class PeerManager {

    private final PeerDelegate peerDelegate;

    public PeerManager(PeerDelegate peerDelegate) {
        this.peerDelegate = peerDelegate;
    }

    public void createPeer(Socket socket) {
    }
}
