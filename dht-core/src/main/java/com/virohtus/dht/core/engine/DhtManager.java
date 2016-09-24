package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.network.GetDhtNetworkFailedException;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.network.event.*;
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
            case DhtProtocol.GET_PREDECESSOR_REQUEST:
                handleGetPredecessorRequest(peerId, (GetPredecessorRequest)event);
                break;
            case DhtProtocol.SET_PREDECESSOR_REQUEST:
                handleSetPredecessorRequest(peerId, (SetPredecessorRequest)event);
                break;
            case DhtProtocol.PREDECESSOR_DIED:
                handlePredecessorDied(peerId, (PredecessorDied)event);
                break;
        }
    }

    public void join(ConnectionInfo connectionInfo) throws IOException {
        Peer peer = dhtNode.openConnection(connectionInfo);

        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        try {
            // cleanup in case we try to rejoin the network
            nodeNetwork.clearSuccessors().forEach(successor -> {
                Peer successorPeer = null;
                try {
                    successorPeer = dhtNode.getPeer(successor, PeerType.OUTGOING);
                    successorPeer.shutdown();
                } catch (PeerNotFoundException e) {
                    LOG.warn("could not find successor peer with nodeIdentity: " + successor);
                }
            });
            nodeNetwork.addSuccessor(peer.getNodeIdentity());
        } catch (InterruptedException e) {
            LOG.error("wait for node identity interrupted when joining network");
            throw new IOException("could not join network because connection timed out" ,e);
        }
        peer.send(new SetPredecessorRequest(dhtNode.getNodeIdentity()));
    }

    public GetDhtNetwork getDhtNetwork() throws GetDhtNetworkFailedException, InterruptedException {
        this.getDhtNetwork.clear();
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        GetDhtNetwork net = new GetDhtNetwork();
        net.addNodeNet(nodeNetwork);
        if(!nodeNetwork.hasSuccessors()) {
            return net;
        }
        NodeIdentity successor = nodeNetwork.getSuccessors().get(0);
        try {
            Peer peer = dhtNode.getPeer(successor, PeerType.OUTGOING);
            peer.send(net);
            return this.getDhtNetwork.get();
        } catch (PeerNotFoundException e) {
            LOG.error("could not find peer with nodeIdentity: " + successor);
            throw new GetDhtNetworkFailedException(e);
        } catch (IOException e) {
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
            switch(peer.getPeerType()) {
                case INCOMING:
                    if (predecessor.isPresent() && predecessor.get().equals(nodeIdentity)) {
                        nodeNetwork.setPredecessor(null);

                        // notify successor that ring is broken
                        // todo mark this node as unstable
                        // todo this needs to change. what happens when there are multiple successors?
                        // this could potentially spin forever
                        // would it be better to let the successor auto-balance?
                        if(nodeNetwork.hasSuccessors()) {
                            NodeIdentity successor = nodeNetwork.getSuccessors().get(0);
                            try {
                                Peer successorPeer = dhtNode.getPeer(successor, PeerType.OUTGOING);
                                successorPeer.send(new PredecessorDied(dhtNode.getNodeIdentity()));
                            } catch (PeerNotFoundException e) {
                                if(dhtNode.isAlive()) {
                                    LOG.info("could not find peer with nodeIdentity: " + successor);
                                }
                            } catch (IOException e) {
                                LOG.info("could not send PredecessorDied to peer with nodeIdentity: " + successor);
                            }
                        }
                    }
                    break;
                case OUTGOING:
                    if (nodeNetwork.getSuccessors().contains(nodeIdentity)) {
                        nodeNetwork.removeSuccessor(nodeIdentity);
                        // todo trigger fix fingers
                    }
                    break;
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
            Peer peer = dhtNode.getPeer(successor, PeerType.OUTGOING);
            peer.send(getDhtNetwork);
        } catch (PeerNotFoundException e) {
            LOG.error("tried to send GetDhtNetwork to nonexistent peer with nodeIdentity: " + nodeNetwork);
        } catch (IOException e) {
            LOG.error("failed to send GetDhtNetwork to peer with nodeIdentity: " + nodeNetwork);
        }
    }

    private void handleGetPredecessorRequest(String peerId, GetPredecessorRequest request) {
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        try {
            Peer peer = dhtNode.getPeer(peerId);
            peer.send(new GetPredecessorResponse(nodeNetwork.getPredecessor().get()));
        } catch (PeerNotFoundException e) {
            LOG.error("could not find peer: " + peerId);
        } catch (IOException e) {
            LOG.error("failed to send GetPredecessorResponse to peer: " + peerId);
        }
    }

    private void handleSetPredecessorRequest(String peerId, SetPredecessorRequest request) {
        NodeIdentity nodeIdentity = request.getNodeIdentity();
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        try {
            if(nodeNetwork.isEmpty()) {
                Peer peer = dhtNode.openConnection(nodeIdentity.getConnectionInfo());
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

    private void handlePredecessorDied(String peerId, PredecessorDied event) {
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        if(!nodeNetwork.hasSuccessors()) {
            // open new connection to the other end
            try {
                dhtNode.joinNetwork(event.getInitiator().getConnectionInfo());
            } catch (IOException e) {
                LOG.warn("received a request to rebuild ring but the initiator seems to have died too... waiting for another request");
            }
        } else {
            // forward it on
            NodeIdentity successor = nodeNetwork.getSuccessors().get(0);
            try {
                Peer successorPeer = dhtNode.getPeer(successor, PeerType.OUTGOING);
                successorPeer.send(event);
            } catch (Exception e) {
                LOG.error("could not forward ProcecssorDied event. this network sucks. reason: " + e.getMessage());
            }
        }
    }
}
