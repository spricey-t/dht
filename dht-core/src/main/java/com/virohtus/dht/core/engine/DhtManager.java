package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.network.event.NodeIdentityRequest;
import com.virohtus.dht.core.network.event.NodeIdentityResponse;
import com.virohtus.dht.core.network.event.SetPredecessorRequest;
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
            case DhtProtocol.SET_PREDECESSOR_REQUEST:
                handleSetPredecessorRequest(peerId, (SetPredecessorRequest)event);
                break;
        }
    }

    public void join(ConnectionInfo connectionInfo) throws IOException {
        Peer peer = dhtNode.openConnection(connectionInfo);
        handlerChain.handle(peer.getPeerId(), new PeerConnected(peer));

        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        if(nodeNetwork.isEmpty()) {
            try {
                nodeNetwork.addSuccessor(peer.getNodeIdentity());
            } catch (InterruptedException e) {
                LOG.error("wait for node identity interrupted when joining network");
                throw new IOException("could not join network because connection timed out" ,e);
            }
        }
        peer.send(new SetPredecessorRequest(dhtNode.getNodeIdentity()));
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

    private void handleSetPredecessorRequest(String peerId, SetPredecessorRequest request) {
        NodeIdentity nodeIdentity = request.getNodeIdentity();
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        try {
            if(nodeNetwork.isEmpty()) {
                Peer peer = dhtNode.openConnection(nodeIdentity.getConnectionInfo());
                handlerChain.handle(peer.getPeerId(), new PeerConnected(peer));
                peer.send(new SetPredecessorRequest(dhtNode.getNodeIdentity()));
            }
            nodeNetwork.setPredecessor(request.getNodeIdentity());
        } catch (IOException e) {
            LOG.error("failed to open connection when handling SetPredecessorRequest " + e.getMessage());
            try {
                dhtNode.getPeer(peerId).shutdown();
            } catch (PeerNotFoundException e1) {
                LOG.error("dafuq? " + e1.getMessage());
            }
        }
    }
}
