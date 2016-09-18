package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.network.event.GetPredecessorRequest;
import com.virohtus.dht.core.network.event.GetPredecessorResponse;
import com.virohtus.dht.core.network.event.SetPredecessorRequest;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DhtStabilizer implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DhtStabilizer.class);

    private final DhtNode dhtNode;
    private final ExecutorService executorService;
    private final Resolvable<NodeIdentity> successorsPredecessor;
    private Future stabilizerFuture;

    public DhtStabilizer(DhtNode dhtNode, ExecutorService executorService) {
        this.dhtNode = dhtNode;
        this.executorService = executorService;
        this.successorsPredecessor = new Resolvable<>(DhtProtocol.NODE_TIMEOUT);
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


    private void stabilize() {
        NodeNetwork nodeNetwork = dhtNode.getNodeNetwork();
        if(!nodeNetwork.hasSuccessors()) {
            return;
        }
        NodeIdentity successor = nodeNetwork.getSuccessors().get(0);
        try {
            Peer successorPeer = dhtNode.getPeer(successor);
            successorPeer.send(new GetPredecessorRequest());
            NodeIdentity potentialNewSuccessor = successorsPredecessor.get();
            if(dhtNode.getNodeIdentity().equals(potentialNewSuccessor)) {
                return;
            }

            Peer newSuccessorPeer = dhtNode.openConnection(potentialNewSuccessor.getConnectionInfo());
            newSuccessorPeer.send(new SetPredecessorRequest(dhtNode.getNodeIdentity()));

            List<NodeIdentity> oldSuccessors = nodeNetwork.clearSuccessors();
            for(NodeIdentity oldSuccessor : oldSuccessors) {
                try {
                    Peer oldPeer = dhtNode.getPeer(oldSuccessor);
                    if(oldPeer.getPeerType().equals(PeerType.OUTGOING)) {
                        oldPeer.shutdown();
                    }
                } catch(PeerNotFoundException e) {
                    LOG.warn("tried to shutdown nonexistent peer with nodeIdentity: " + oldSuccessor);
                }
            }
            nodeNetwork.addSuccessor(potentialNewSuccessor);
        } catch (Exception e) {
            LOG.error("error when performing stabilization: " + e.getMessage());
        }
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.GET_PREDECESSOR_RESPONSE:
                handleGetPredecessorResponse(peerId, (GetPredecessorResponse)event);
                break;
        }
    }

    private void handleGetPredecessorResponse(String peerId, GetPredecessorResponse response) {
        successorsPredecessor.resolve(response.getPredecessor());
    }
}
