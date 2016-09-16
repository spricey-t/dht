package com.virohtus.dht.core.handler;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import com.virohtus.dht.core.transport.server.event.ServerStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHandler.class);

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.SERVER_START:
                LOG.info("server started on port " + ((ServerStart)event).getPort());
                break;
            case DhtProtocol.SERVER_SHUTDOWN:
                LOG.info("server shutdown");
                break;
            case DhtProtocol.SOCKET_CONNECT:
                break;
            case DhtProtocol.PEER_CONNECTED:
                handlePeerConnected(peerId, (PeerConnected)event);
                break;
            case DhtProtocol.PEER_DISCONNECTED:
                LOG.info("peer disconnected: " + ((PeerDisconnected)event).getPeer().toString());
                break;
        }
    }

    private void handlePeerConnected(String peerId, PeerConnected event) {
        Peer peer = event.getPeer();
        try {
            LOG.info("peer connected: " + peer.getPeerId() + " nodeId: " + peer.getNodeIdentity().getNodeId());
        } catch (InterruptedException e) {
            LOG.info("timed out waiting for node identity for peer: " + peerId);
        }
    }

}
