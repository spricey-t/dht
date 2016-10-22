package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.network.*;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeManager;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;
import com.virohtus.dht.core.network.peer.PeerType;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class StabilizationStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizationStore.class);
    private final ExecutorService executorService;
    private final NodeManager nodeManager;
    private final PeerStore peerStore;
    private Future future;

    public StabilizationStore(ExecutorService executorService, NodeManager nodeManager, PeerStore peerStore) {
        this.executorService = executorService;
        this.nodeManager = nodeManager;
        this.peerStore = peerStore;
    }

    public void start() {
        if(isAlive()) {
            return;
        }
        future = executorService.submit(() -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(DhtProtocol.STABILIZATION_PERIOD);
                    stabilize();
                }
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void shutdown() {
        if(!isAlive()) {
            return;
        }
        future.cancel(true);
    }

    public boolean isAlive() {
        return future != null && !future.isCancelled() && !future.isDone();
    }

    @Override
    public void onAction(Action action) {
        if(action instanceof TransportableAction) {
            TransportableAction transportableAction = (TransportableAction)action;
            switch (transportableAction.getType()) {
                case DhtProtocol.SET_PREDECESSOR:
                    handleSetPredecessor((SetPredecessor)transportableAction);
                    break;
            }
        }
    }


    private void stabilize() {
        try {
            Node node = nodeManager.getCurrentNode();
            if(!node.getFingerTable().hasSuccessors()) {
                return;
            }
            updateImmediateSuccessor(node);
        } catch (Exception e) {
            LOG.error("stabilization error", e);
        }
    }

    private void updateImmediateSuccessor(Node node) throws PeerNotFoundException, IOException, TimeoutException, InterruptedException {
        Peer successor = peerStore.getPeer(node.getFingerTable().getImmediateSuccessor());
        Node successorNode = successor.sendRequest(new GetNodeRequest(), GetNodeResponse.class).get().getNode();
        Node successorsPredecessor = successorNode.getFingerTable().getPredecessor();
        if(successorsPredecessor == null) {
            successor.send(new SetPredecessor(node).serialize());
            return;
        }
        if(successorsPredecessor.getNodeIdentity().equals(node.getNodeIdentity())) {
            return;
        }
        Peer newSuccessor = peerStore.createPeer(successorsPredecessor.getNodeIdentity().getSocketAddress());
        node.getFingerTable().setImmediateSuccessor(successorsPredecessor); //set for successor to have up to date node info
        newSuccessor.send(new SetPredecessor(node).serialize());
        nodeManager.setImmediateSuccessor(successorsPredecessor); // set for real, since send was successful
        nodeManager.removeSuccessor(successor.getNodeIdentity());
        if(successorNode.getFingerTable().getImmediateSuccessor().getNodeIdentity().equals(node.getNodeIdentity())) {
            successor.setType(PeerType.INCOMING);
        } else {
            successor.shutdown();
        }
    }

    private void handleSetPredecessor(SetPredecessor setPredecessor) {
        nodeManager.setPredecessor(setPredecessor.getNode());
    }
}
