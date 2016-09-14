package com.virohtus.dht.core.peer.handler;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerPool;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.peer.event.PeerDetailsResponse;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerPoolHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PeerPoolHandler.class);
    private final PeerPool peerPool;

    public PeerPoolHandler(PeerPool peerPool) {
        this.peerPool = peerPool;
    }

    @Override
    public void handle(Event event) {
        switch (event.getType()) {
            case DhtProtocol.PEER_CONNECTED:
                handlePeerConnected((PeerConnected)event);
                break;
            case DhtProtocol.PEER_DISCONNECTED:
                handlePeerDisconnected((PeerDisconnected)event);
                break;
        }
    }

    private void handlePeerConnected(PeerConnected event) {
        peerPool.addPeer(event.getPeer());
    }

    private void handlePeerDisconnected(PeerDisconnected event) {
        try {
            peerPool.removePeer(event.getPeer().getPeerId());
        } catch (PeerNotFoundException e) {
            LOG.error("tried to remove non existent peer: " + event.getPeer());
        }
    }
}
