package com.virohtus.dht.node;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class PeerManager {

    private final ExecutorService executorService;
    private final PeerManagerDelegate peerManagerDelegate;
    private final Map<String, Peer> peers;

    public PeerManager(ExecutorService executorService, PeerManagerDelegate peerManagerDelegate) {
        this.executorService = executorService;
        this.peerManagerDelegate = peerManagerDelegate;
        this.peers = new HashMap<>();
    }

    public Peer createPeer(Socket socket) throws IOException {
        Peer peer = new Peer(this, executorService, socket);
        synchronized (peers) {
            peers.put(peer.getId(), peer);
        }
        return peer;
    }

    public void onPeerDisconnect(Peer peer) {
        synchronized (peers) {
            peers.remove(peer.getId());
        }
        peerManagerDelegate.peerDisconnected(peer);
    }
}
