package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.event.NodeIdentityRequest;
import com.virohtus.dht.core.network.event.NodeIdentityResponse;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DhtNodeManager implements Manager {

    private static final Logger LOG = LoggerFactory.getLogger(DhtNodeManager.class);
    private final DhtNode dhtNode;
    private final PeerManager peerManager;

    public DhtNodeManager(DhtNode dhtNode, PeerManager peerManager) {
        this.dhtNode = dhtNode;
        this.peerManager = peerManager;
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.NODE_IDENTITY_REQUEST:
                handleNodeIdentityRequest(peerId, (NodeIdentityRequest)event);
                break;
        }
    }

    private void handleNodeIdentityRequest(String peerId, NodeIdentityRequest request) {
        try {
            Peer peer = peerManager.getPeer(peerId);
            peer.send(new NodeIdentityResponse(request.getRequestId(), dhtNode.getNodeIdentity()));
        } catch (PeerNotFoundException e) {
            LOG.warn("received NodeIdentityRequest from an unmanaged peer: " + peerId);
        } catch (IOException e) {
            LOG.warn("failed to send NodeIdentityResponse to peer: " + peerId);
        }
    }

}
