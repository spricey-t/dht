package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNodeManager;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.network.*;
import com.virohtus.dht.core.engine.action.peer.PeerDisconnected;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.Keyspace;
import com.virohtus.dht.core.network.Network;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;
import com.virohtus.dht.core.network.peer.PeerType;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class NetworkStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkStore.class);
    private final DhtNodeManager dhtNodeManager;
    private final PeerStore peerStore;
    private final Resolvable<Network> networkResolvable;
    private final Object networkLock;

    public NetworkStore(DhtNodeManager dhtNodeManager, PeerStore peerStore) {
        this.dhtNodeManager = dhtNodeManager;
        this.peerStore = peerStore;
        this.networkResolvable = new Resolvable<>(DhtProtocol.NETWORK_TIMEOUT);
        this.networkLock = new Object();
    }

    public void setImmediateSuccessor(Peer peer) throws IOException, TimeoutException, InterruptedException {
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            JoinNetworkResponse response = peer.sendRequest(new JoinNetworkRequest(node), JoinNetworkResponse.class).get();
            Node successor = response.getNode();
            node.getFingerTable().setImmediateSuccessor(successor);
            node.setKeyspace(successor.getFingerTable().getPredecessor().getKeyspace());
        }
    }

    public void setPredecessor(Node predecessor) {
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            Keyspace lowerHalf = node.getKeyspace().split();
            if(predecessor.getKeyspace().isDefaultKeyspace()) {
                predecessor.setKeyspace(lowerHalf);
            } else {
                predecessor.getKeyspace().merge(lowerHalf);
            }
            node.getFingerTable().setPredecessor(predecessor);
        }
    }

    public void joinNetwork(SocketAddress socketAddress) throws IOException, InterruptedException, TimeoutException {
        Peer peer = peerStore.createPeer(socketAddress);
        synchronized (networkLock) {
            setImmediateSuccessor(peer);
            Node node = dhtNodeManager.getNode();
            Node successorsSuccessor = node.getFingerTable().getImmediateSuccessor().getFingerTable().getImmediateSuccessor();
            if(successorsSuccessor.getNodeIdentity().equals(node.getNodeIdentity())) {
                node.getFingerTable().setPredecessor(node.getFingerTable().getImmediateSuccessor());
            }
        }
    }

    public Network getNetwork() throws InterruptedException, TimeoutException, PeerNotFoundException, IOException {
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            GetNetwork getNetwork = new GetNetwork(node);
            if (!node.getFingerTable().hasSuccessors()) {
                return getNetwork.getNetwork();
            }
            Peer successor = peerStore.getPeer(node.getFingerTable().getImmediateSuccessor());
            successor.send(getNetwork.serialize());
        }
        return networkResolvable.get();
    }

    public void stabilize() throws InterruptedException, TimeoutException, PeerNotFoundException, IOException {
        Peer oldSuccessor;
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            if (!node.getFingerTable().hasSuccessors()) {
                return;
            }
            oldSuccessor = peerStore.getPeer(node.getFingerTable().getImmediateSuccessor());
        }
        Node oldSuccessorNode = oldSuccessor.sendRequest(new GetNodeRequest(), GetNodeResponse.class).get().getNode();
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            if (oldSuccessorNode.getNodeIdentity().equals(node.getNodeIdentity())) {
                return;
            }
        }
        Peer newSuccessor;
        try {
            newSuccessor = peerStore.getPeer(oldSuccessorNode.getFingerTable().getPredecessor());
        } catch (PeerNotFoundException e) {
            newSuccessor = peerStore.createPeer(oldSuccessorNode.getFingerTable().getPredecessor().getNodeIdentity().getSocketAddress());
        }
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            setImmediateSuccessor(newSuccessor);
            if(oldSuccessor.getNodeIdentity().equals(node.getFingerTable().getPredecessor().getNodeIdentity())) {
                oldSuccessor.setType(PeerType.INCOMING);
            } else {
                oldSuccessor.shutdown();
            }
        }
    }

    @Override
    public void onAction(Action action) {
        if(action instanceof TransportableAction) {
            TransportableAction transportableAction = (TransportableAction)action;
            switch (transportableAction.getType()) {
                case DhtProtocol.JOIN_NETWORK_REQUEST:
                    handleJoinNetworkRequest((JoinNetworkRequest)transportableAction);
                    break;
                case DhtProtocol.GET_NODE_IDENTITY_REQUEST:
                    handleGetNodeIdentityRequest((GetNodeIdentityRequest)transportableAction);
                    break;
                case DhtProtocol.GET_NETWORK:
                    handleGetNetwork((GetNetwork)transportableAction);
                    break;
                case DhtProtocol.GET_NODE_REQUEST:
                    handleGetNodeRequest((GetNodeRequest)transportableAction);
                    break;
            }
        }

        if(action instanceof PeerDisconnected) {
            handlePeerDisconnected((PeerDisconnected)action);
        }
    }

    private void handlePeerDisconnected(PeerDisconnected peerDisconnected) {
        Peer peer = peerDisconnected.getPeer();
        if(!peer.hasNodeIdentity()) {
            return;
        }

        try {
            synchronized (networkLock) {
                Node node = dhtNodeManager.getNode();
                Optional<Node> potentialSuccessor = node.getFingerTable().getSuccessor(peer.getNodeIdentity());
                if(potentialSuccessor.isPresent()) {
                    node.getFingerTable().removeSuccessor(peer.getNodeIdentity());
                }
                if(node.getFingerTable().getPredecessor().getNodeIdentity().equals(peer.getNodeIdentity())) {
                    Node predecessor = node.getFingerTable().getPredecessor();
                    node.getFingerTable().setPredecessor(null);
                    node.getKeyspace().merge(predecessor.getKeyspace());
                }
            }
        } catch (Exception e) {
            LOG.error("error during network cleanup for peer: " + peer.getId(), e);
        }
    }

    private void handleJoinNetworkRequest(JoinNetworkRequest request) {
        if(!request.hasSourcePeer()) {
            LOG.error("received JoinNetworkRequest from null peer!");
            return;
        }
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            setPredecessor(request.getNode());
            if(!node.getFingerTable().hasSuccessors()) {
                node.getFingerTable().setImmediateSuccessor(request.getNode());
            }
            try {
                request.getSourcePeer().send(new JoinNetworkResponse(request.getRequestId(), node).serialize());
            } catch (IOException e) {
                LOG.error("failed to send JoinNetworkResponse to peer: " + request.getSourcePeer().getId());
            }
        }
    }

    private void handleGetNodeIdentityRequest(GetNodeIdentityRequest request) {
        if(!request.hasSourcePeer()) {
            LOG.error("received GetNodeIdentityRequest from null peer!");
            return;
        }

        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            try {
                request.getSourcePeer().send(new GetNodeIdentityResponse(request.getRequestId(), node.getNodeIdentity()).serialize());
            } catch (IOException e) {
                LOG.error("failed to send GetNodeIdentityResponse to peer: " + request.getSourcePeer().getId(), e);
            }
        }
    }

    private void handleGetNodeRequest(GetNodeRequest request) {
        if(!request.hasSourcePeer()) {
            LOG.error("received GetNodeRequest from null peer!");
            return;
        }
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            try {
                request.getSourcePeer().send(new GetNodeResponse(request.getRequestId(), node).serialize());
            } catch (IOException e) {
                LOG.error("failed to send GetNodeResponse to peer: " + request.getSourcePeer().getId(), e);
            }
        }
    }

    private void handleGetNetwork(GetNetwork getNetwork) {
        List<Node> nodes = getNetwork.getNetwork().getNodes();
        if(nodes.isEmpty()) {
            LOG.warn("received GetNetwork request, but nobody is participating!");
            return;
        }
        synchronized (networkLock) {
            Node node = dhtNodeManager.getNode();
            if (nodes.get(0).getNodeIdentity().equals(node.getNodeIdentity())) {
                networkResolvable.resolve(getNetwork.getNetwork());
                return;
            }
            if (!node.getFingerTable().hasSuccessors()) {
                LOG.warn("received GetNetwork request but we have nowhere to go!");
                return;
            }
            try {
                Peer successor = peerStore.getPeer(node.getFingerTable().getImmediateSuccessor());
                getNetwork.getNetwork().addNode(node);
                successor.send(getNetwork.serialize());
            } catch (Exception e) {
                LOG.error("could not forward GetNetwork to immediate successor!");
            }
        }
    }
}
