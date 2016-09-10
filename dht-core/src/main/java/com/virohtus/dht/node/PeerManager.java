package com.virohtus.dht.node;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class PeerManager {

    private final ExecutorService executorService;
    private final PeerManagerDelegate peerManagerDelegate;

    public PeerManager(ExecutorService executorService, PeerManagerDelegate peerManagerDelegate) {
        this.executorService = executorService;
        this.peerManagerDelegate = peerManagerDelegate;
    }

    public Peer createPeer(Socket socket) throws IOException {
        Peer peer = new Peer(this, executorService, socket);
        return peer;
    }

    public void onPeerDisconnect(Peer peer) {
        peerManagerDelegate.peerDisconnected(peer);
    }
}
