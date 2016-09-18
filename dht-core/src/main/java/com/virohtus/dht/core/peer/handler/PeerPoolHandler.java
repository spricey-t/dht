package com.virohtus.dht.core.peer.handler;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.network.event.NodeIdentityResponse;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerPool;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import com.virohtus.dht.core.transport.server.event.ServerShutdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerPoolHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PeerPoolHandler.class);
    private final PeerPool peerPool;
    private final Object shutdownLock;

    public PeerPoolHandler(PeerPool peerPool) {
        this.peerPool = peerPool;
        this.shutdownLock = new Object();
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.SERVER_SHUTDOWN:
                handleServerShutdown((ServerShutdown)event);
                break;
            case DhtProtocol.PEER_CONNECTED:
                handlePeerConnected((PeerConnected)event);
                break;
            case DhtProtocol.PEER_DISCONNECTED:
                handlePeerDisconnected((PeerDisconnected)event);
                break;
            case DhtProtocol.NODE_IDENTITY_RESPONSE:
                handleNodeIdentityResponse(peerId, (NodeIdentityResponse)event);
                break;
        }
    }

    private void handleServerShutdown(ServerShutdown event) {
        synchronized (shutdownLock) {
            if(peerPool.listPeers().isEmpty()) {
                return;
            }
            peerPool.listPeers().stream().forEach(Peer::shutdown);
            try {
                shutdownLock.wait();
            } catch (InterruptedException e) {
                LOG.warn("peer pool shutdown interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void handlePeerConnected(PeerConnected event) {
        Peer peer = event.getPeer();
        peerPool.addPeer(peer);
        // trigger loading node information
        try {
            peer.getNodeIdentity();
        } catch (InterruptedException e) {
            LOG.warn("timed out waiting for node identity for peer: " + peer.getPeerId());
            peer.shutdown();
        }
    }

    private void handlePeerDisconnected(PeerDisconnected event) {
        try {
            peerPool.removePeer(event.getPeer().getPeerId());
            synchronized (shutdownLock) {
                if(peerPool.listPeers().isEmpty()) {
                    shutdownLock.notifyAll();
                }
            }
        } catch (PeerNotFoundException e) {
            LOG.error("tried to remove non existent peer: " + event.getPeer());
        }
    }

    private void handleNodeIdentityResponse(String peerId, NodeIdentityResponse response) {
        try {
            peerPool.getPeer(peerId).nodeIdentity.resolve(response.getNodeIdentity());
        } catch (PeerNotFoundException e) {
            LOG.warn("received node identity for nonexistent peer: " + peerId);
        }
    }
}
