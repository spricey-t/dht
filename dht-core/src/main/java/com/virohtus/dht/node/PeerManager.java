package com.virohtus.dht.node;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.event.Event;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class PeerManager implements PeerDelegate {

    private final ExecutorService executorService;
    private final PeerDelegate peerDelegate;
    private final Map<String, Peer> peers;

    public PeerManager(ExecutorService executorService, PeerDelegate peerDelegate) {
        this.executorService = executorService;
        this.peerDelegate = peerDelegate;
        this.peers = new HashMap<>();
    }

    @Override
    public void peerEventReceived(Peer peer, Event event) {
        peerDelegate.peerEventReceived(peer, event);
    }

    @Override
    public void peerDisconnected(Peer peer) {
        synchronized (peers) {
            peers.remove(peer.getId());
        }
        peerDelegate.peerDisconnected(peer);
    }

    public Peer createPeer(Socket socket) throws IOException {
        Peer peer = new Peer(this, executorService, socket);
        synchronized (peers) {
            peers.put(peer.getId(), peer);
        }
        return peer;
    }

    public Peer getPeerByConnectionDetails(ConnectionDetails connectionDetails) {
        synchronized (peers) {
            return peers.values()
                    .stream()
                    .filter(peer -> peer.getConnectionDetails().equals(connectionDetails))
                    .findFirst()
                    .get();
        }
    }
}
