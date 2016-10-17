package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNodeManager;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.network.*;
import com.virohtus.dht.core.engine.action.peer.PeerDisconnected;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.*;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class NetworkStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkStore.class);
    private final DhtNodeManager dhtNodeManager;
    private final NodeManager nodeManager;
    private final PeerStore peerStore;
    private final Resolvable<Network> networkResolvable;
    private final Object networkLock;

    public NetworkStore(DhtNodeManager dhtNodeManager, NodeManager nodeManager, PeerStore peerStore) {
        this.dhtNodeManager = dhtNodeManager;
        this.nodeManager = nodeManager;
        this.peerStore = peerStore;
        this.networkResolvable = new Resolvable<>(DhtProtocol.NETWORK_TIMEOUT);
        this.networkLock = new Object();
    }

    public void join(SocketAddress socketAddress) throws IOException, TimeoutException, InterruptedException {
        Peer peer = peerStore.createPeer(socketAddress);
        join(peer);
    }

    public void join(Peer peer) throws IOException, TimeoutException, InterruptedException {
        JoinNetworkResponse response = peer.sendRequest(new JoinNetworkRequest(nodeManager.getCurrentNode()), JoinNetworkResponse.class).get();
        Node successor = response.getNode();
        Node successorsPredecessor = successor.getFingerTable().getPredecessor();
        Node node = nodeManager.getCurrentNode();
        nodeManager.setKeyspace(successorsPredecessor.getKeyspace());
        nodeManager.setImmediateSuccessor(successor);
        if(successor.getFingerTable().getImmediateSuccessor().getNodeIdentity().equals(node.getNodeIdentity()))  {
            nodeManager.setPredecessor(successor);
        }
    }

    public Network getNetwork() throws IOException, TimeoutException, InterruptedException {
        networkResolvable.clear();
        Node node = nodeManager.getCurrentNode();
        Network network = new Network(Arrays.asList(node));
        if(!node.getFingerTable().hasSuccessors()) {
            return network;
        }
        try {
            Peer successor = peerStore.getPeer(node.getFingerTable().getImmediateSuccessor());
            successor.send(new GetNetwork(network).serialize());
        } catch (PeerNotFoundException e) {
            throw new IOException(e);
        }
        return networkResolvable.get();
    }

    @Override
    public void onAction(Action action) {
        if(action instanceof PeerDisconnected) {
            handlePeerDisconnected((PeerDisconnected)action);
            return;
        }
        if(action instanceof TransportableAction) {
            TransportableAction transportableAction = (TransportableAction)action;
            switch (transportableAction.getType()) {
                case DhtProtocol.GET_NODE_IDENTITY_REQUEST:
                    handleGetNodeIdentityRequest((GetNodeIdentityRequest)transportableAction);
                    break;
                case DhtProtocol.JOIN_NETWORK_REQUEST:
                    handleJoinNetworkRequest((JoinNetworkRequest)transportableAction);
                    break;
                case DhtProtocol.GET_NETWORK:
                    handleGetNetwork((GetNetwork)transportableAction);
                    break;
                case DhtProtocol.GET_NODE_REQUEST:
                    handleGetNodeRequest((GetNodeRequest)transportableAction);
                    break;
            }
        }
    }

    private void handlePeerDisconnected(PeerDisconnected peerDisconnected) {
        Peer peer = peerDisconnected.getPeer();
        Node node = nodeManager.getCurrentNode();
        Node predecessor = node.getFingerTable().getPredecessor();
        if(predecessor != null && predecessor.getNodeIdentity().equals(peer.getNodeIdentity())) {
            nodeManager.mergeKeyspace(predecessor.getKeyspace());
            nodeManager.setPredecessor(null);
        }
        nodeManager.removeSuccessor(peer.getNodeIdentity());
        node = nodeManager.getCurrentNode();
        if(node.getFingerTable().getPredecessor() != null && !node.getFingerTable().hasSuccessors() && !dhtNodeManager.isShutdown()) {
            nodeManager.setImmediateSuccessor(predecessor);
        }
    }

    private void handleGetNodeIdentityRequest(GetNodeIdentityRequest request) {
        try {
            request.getSourcePeer().send(new GetNodeIdentityResponse(request.getRequestId(),
                    nodeManager.getCurrentNode().getNodeIdentity()).serialize());
        } catch (IOException e) {
            LOG.error("failed to send GetNodeIdentityResponse to peer: " + request.getSourcePeer().getId());
        }
    }

    private void handleJoinNetworkRequest(JoinNetworkRequest request) {
        Node node = nodeManager.getCurrentNode();
        Keyspace[] splitKeyspace = node.getKeyspace().split();
        Node predecessor = request.getNode();
        if(predecessor.getKeyspace().isDefaultKeyspace()) {
            predecessor.setKeyspace(splitKeyspace[0]);
        } else {
            predecessor.getKeyspace().merge(splitKeyspace[0]);
        }
        nodeManager.setKeyspace(splitKeyspace[1]);
        nodeManager.setPredecessor(predecessor);
        if(!node.getFingerTable().hasSuccessors()) {
            nodeManager.setImmediateSuccessor(predecessor);
        }
        try {
            request.getSourcePeer().send(new JoinNetworkResponse(request.getRequestId(), nodeManager.getCurrentNode()).serialize());
        } catch (IOException e) {
            LOG.error("failed to send JoinNetworkResponse");
        }
    }

    private void handleGetNetwork(GetNetwork getNetwork) {
        Node node = nodeManager.getCurrentNode();
        Network network = getNetwork.getNetwork();
        if(network.getNodes().get(0).getNodeIdentity().equals(node.getNodeIdentity())) {
            networkResolvable.resolve(network);
            return;
        }
        network.addNode(node);
        try {
            Peer peer = peerStore.getPeer(node.getFingerTable().getImmediateSuccessor());
            peer.send(getNetwork.serialize());
        } catch (Exception e) {
            LOG.warn("received GetNetwork but we have nowhere to go!");
        }
    }

    private void handleGetNodeRequest(GetNodeRequest getNodeRequest) {
        try {
            getNodeRequest.getSourcePeer().send(new GetNodeResponse(getNodeRequest.getRequestId(),
                    nodeManager.getCurrentNode()).serialize());
        } catch (IOException e) {
            LOG.error("failed to send GetNodeResponse to peer: " + getNodeRequest.getSourcePeer().getId(), e);
        }
    }
}
