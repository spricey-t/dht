package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.event.NodeIdentityRequest;
import com.virohtus.dht.core.network.event.NodeIdentityResponse;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class DhtManager implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DhtManager.class);
    private final HandlerChain handlerChain;
    private final ExecutorService executorService;
    private final DhtNode dhtNode;

    public DhtManager(HandlerChain handlerChain, ExecutorService executorService, DhtNode dhtNode) {
        this.handlerChain = handlerChain;
        this.executorService = executorService;
        this.dhtNode = dhtNode;
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.PEER_CONNECTED:
                handlePeerConnected((PeerConnected)event);
                break;
            case DhtProtocol.NODE_IDENTITY_REQUEST:
                handleNodeIdentityRequest(peerId, (NodeIdentityRequest)event);
                break;
        }
    }

    public void join(ConnectionInfo connectionInfo) throws IOException {
        Peer peer = new Peer(handlerChain, executorService, PeerType.OUTGOING,
                new Socket(connectionInfo.getHost(), connectionInfo.getPort()));
        handlerChain.handle(peer.getPeerId(), new PeerConnected(peer));
    }

    private void handlePeerConnected(PeerConnected peerConnected) {
    }

    private void handleNodeIdentityRequest(String peerId, NodeIdentityRequest request) {
        try {
            Peer peer = dhtNode.getPeer(peerId);
            try {
                peer.send(new NodeIdentityResponse(new NodeIdentity(
                        dhtNode.getNodeId(),
                        dhtNode.getConnectionInfo()
                )));
            } catch (IOException e) {
                LOG.error("failed to send NodeIdentityResponse to peer: " + peerId);
                peer.shutdown();
            }
        } catch (PeerNotFoundException e) {
            LOG.error("received NodeIdentityRequest for nonexistent peer: " + peerId);
        }
    }
}
