package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.network.event.*;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DhtStabilizer implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DhtStabilizer.class);

    private final DhtNode dhtNode;
    private final ExecutorService executorService;
    private final Resolvable<NodeNetwork> successorsNodeNetwork;
    private Future stabilizerFuture;

    public DhtStabilizer(DhtNode dhtNode, ExecutorService executorService) {
        this.dhtNode = dhtNode;
        this.executorService = executorService;
        this.successorsNodeNetwork = new Resolvable<>(DhtProtocol.NODE_TIMEOUT);
    }

    public void start() {
        if(isAlive()) {
            return;
        }
        stabilizerFuture = executorService.submit(() -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(DhtProtocol.STABILIZATION_PERIOD);
                    stabilize();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void shutdown() {
        if(!isAlive()) {
            return;
        }
        stabilizerFuture.cancel(true);
    }

    public boolean isAlive() {
        return stabilizerFuture != null &&
                !stabilizerFuture.isCancelled() && !stabilizerFuture.isDone();
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.GET_NODE_NETWORK_REQUEST:
                handleGetNodeNetworkRequest(peerId, (GetNodeNetworkRequest)event);
                break;
            case DhtProtocol.GET_NODE_NETWORK_RESPONSE:
                handleGetNodeNetworkResponse(peerId, (GetNodeNetworkResponse)event);
                break;
        }
    }

    private void stabilize() {
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        if(!nodeNetwork.hasSuccessors()) {
            return;
        }
        NodeIdentity successor = nodeNetwork.getSuccessors().get(0);
        try {
            successorsNodeNetwork.clear();
            Peer successorPeer = dhtNode.getPeer(successor, PeerType.OUTGOING);
            successorPeer.send(new GetNodeNetworkRequest());
            Optional<NodeIdentity> successorsPredecessor = successorsNodeNetwork.get().getPredecessor();
            if(!successorsPredecessor.isPresent()) {
                LOG.error("something went wrong, my successor doesn't have a predecessor!");
                return;
            }
            if(dhtNode.getNodeIdentity().equals(successorsPredecessor.get())) {
                List<NodeIdentity> successorsSuccessors = successorsNodeNetwork.get().getSuccessors();
                if(successorsSuccessors.size() == 0) {
                    LOG.error("my successor doesn't have any successors!");
                    return;
                }
                if(successorsSuccessors.get(0).equals(dhtNode.getNodeIdentity())) {
                    // if my successor links back to me we're done
                    return;
                }
//                successorPeer.send( some new event that  gets successors successors successor);
                // todo figure out how to get succesor's finger successors -- and know when to stop
                return;
            }

            Peer newSuccessorPeer = dhtNode.openConnection(successorsPredecessor.get().getConnectionInfo());
            newSuccessorPeer.send(new SetPredecessorRequest(dhtNode.getNodeIdentity()));

            List<NodeIdentity> oldSuccessors = nodeNetwork.clearSuccessors();
            for(NodeIdentity oldSuccessor : oldSuccessors) {
                try {
                    Peer oldPeer = dhtNode.getPeer(oldSuccessor, PeerType.OUTGOING);
                    oldPeer.shutdown();
                } catch(PeerNotFoundException e) {
                    LOG.warn("tried to shutdown nonexistent peer with nodeIdentity: " + oldSuccessor);
                }
            }
            nodeNetwork.addSuccessor(successorsPredecessor.get());
        } catch (Exception e) {
            LOG.error("error when performing stabilization: " + e.getMessage());
        }
    }

    private void handleGetNodeNetworkRequest(String peerId, GetNodeNetworkRequest request) {
        try {
            Peer peer = dhtNode.getPeer(peerId);
            peer.send(new GetNodeNetworkResponse(dhtNode.getNodeNetwork()));
        } catch (PeerNotFoundException e) {
            LOG.error("received GetNodeNetworkRequest for nonexistend peer: " + peerId);
        } catch (IOException e) {
            LOG.error("failed to send GetNodeNetworkResponse to peer: " + peerId);
        }
    }

    private void handleGetNodeNetworkResponse(String peerId, GetNodeNetworkResponse response) {
        successorsNodeNetwork.resolve(response.getNodeNetwork());
    }
}
