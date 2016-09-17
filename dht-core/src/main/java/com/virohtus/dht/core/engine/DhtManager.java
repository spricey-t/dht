package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.network.GetDhtNetworkFailedException;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.network.event.GetDhtNetwork;
import com.virohtus.dht.core.network.event.NodeIdentityRequest;
import com.virohtus.dht.core.network.event.NodeIdentityResponse;
import com.virohtus.dht.core.network.event.SetPredecessorRequest;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class DhtManager implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DhtManager.class);
    private static final long GET_DHT_NETWORK_TIMEOUT = 30000; // 30 seconds

    private final HandlerChain handlerChain;
    private final ExecutorService executorService;
    private final DhtNode dhtNode;
    private final Resolvable<GetDhtNetwork> getDhtNetwork;

    public DhtManager(HandlerChain handlerChain, ExecutorService executorService, DhtNode dhtNode) {
        this.handlerChain = handlerChain;
        this.executorService = executorService;
        this.dhtNode = dhtNode;
        this.getDhtNetwork = new Resolvable<>(GET_DHT_NETWORK_TIMEOUT);
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.PEER_CONNECTED:
                handlePeerConnected((PeerConnected)event);
                break;
            case DhtProtocol.PEER_DISCONNECTED:
                handlePeerDisconnected(peerId, (PeerDisconnected)event);
                break;
            case DhtProtocol.NODE_IDENTITY_REQUEST:
                handleNodeIdentityRequest(peerId, (NodeIdentityRequest)event);
                break;
            case DhtProtocol.GET_DHT_NETWORK:
                handleGetDhtNetwork(peerId, (GetDhtNetwork)event);
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

    public GetDhtNetwork getDhtNetwork() throws GetDhtNetworkFailedException {
        this.getDhtNetwork.clear();
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        GetDhtNetwork net = new GetDhtNetwork();
        net.addNodeNet(nodeNetwork);
        if(!nodeNetwork.hasSuccessors()) {
            return net;
        }
        NodeIdentity successor = nodeNetwork.getSuccessors().get(0);
        try {
            Peer peer = dhtNode.getPeer(successor);
            peer.send(net);
            return this.getDhtNetwork();
        } catch (Exception e) {
            LOG.error("failed to send GetDhtNetwork to peer with nodeIdentity: " + successor + " cause: " + e.getMessage());
            throw new GetDhtNetworkFailedException(e);
        }
    }

    private void handlePeerConnected(PeerConnected peerConnected) {
    }

    private void handlePeerDisconnected(String peerId, PeerDisconnected peerDisconnected) {
        Peer peer = peerDisconnected.getPeer();
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        Optional<NodeIdentity> predecessor = nodeNetwork.getPredecessor();
        try {
            NodeIdentity nodeIdentity = peer.getNodeIdentity();
            if(predecessor.isPresent() && predecessor.get().equals(nodeIdentity)) {
                nodeNetwork.setPredecessor(null);
            }
            if(nodeNetwork.getSuccessors().contains(nodeIdentity)) {
                nodeNetwork.removeSuccessor(nodeIdentity);
                // todo trigger fix fingers
            }
        } catch (InterruptedException e) {
            LOG.warn("wait for node identity interrupted");
        }
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

    private void handleGetDhtNetwork(String peerId, GetDhtNetwork getDhtNetwork) {
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();

        if(!getDhtNetwork.getNodeNets().isEmpty()) {
            NodeNetwork firstNodeNet = getDhtNetwork.getNodeNets().get(0);
            if(dhtNode.getNodeNetwork().equals(firstNodeNet)) {
                // we're done
                LOG.info("completed GetDhtNetwork");
                this.getDhtNetwork.resolve(getDhtNetwork);
                return;
            }
        }

        getDhtNetwork.addNodeNet(nodeNetwork);
        if(nodeNetwork.getSuccessors().isEmpty()) {
            LOG.warn("received GetDhtNetwork but we have no where to go");
            return;
        }
        NodeIdentity successor = nodeNetwork.getSuccessors().get(0);
        try {
            Peer peer = dhtNode.getPeer(successor);
            peer.send(getDhtNetwork);
        } catch (PeerNotFoundException e) {
            LOG.error("tried to send GetDhtNetwork to nonexistent peer with nodeIdentity: " + nodeNetwork);
        } catch (IOException e) {
            LOG.error("failed to send GetDhtNetwork to peer with nodeIdentity: " + nodeNetwork);
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
                nodeNetwork.addSuccessor(nodeIdentity);
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
