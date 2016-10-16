package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNodeManager;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.network.GetNodeRequest;
import com.virohtus.dht.core.engine.action.network.GetNodeResponse;
import com.virohtus.dht.core.engine.action.network.GetPredecessorRequest;
import com.virohtus.dht.core.engine.action.network.GetPredecessorResponse;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.FingerTable;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerType;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class StabilizationStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizationStore.class);
    private final ExecutorService executorService;
    private final DhtNodeManager dhtNodeManager;
    private final PeerStore peerStore;
    private final NetworkStore networkStore;
    private Future future;

    public StabilizationStore(ExecutorService executorService, DhtNodeManager dhtNodeManager, PeerStore peerStore, NetworkStore networkStore) {
        this.executorService = executorService;
        this.dhtNodeManager = dhtNodeManager;
        this.peerStore = peerStore;
        this.networkStore = networkStore;
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
                case DhtProtocol.GET_PREDECESSOR_REQUEST:
                    handleGetPredecessorRequest((GetPredecessorRequest) transportableAction);
                    break;
            }
        }
    }


    private void stabilize() {
        Node node = dhtNodeManager.getNode();
        synchronized (node) {
            FingerTable fingerTable = node.getFingerTable();
            if (!fingerTable.hasSuccessors()) {
                return;
            }
            try {
                Peer successor = peerStore.getPeer(fingerTable.getImmediateSuccessor());
                GetNodeResponse response = successor.sendRequest(new GetNodeRequest(), GetNodeResponse.class).get();
                FingerTable successorsFingerTable = response.getNode().getFingerTable();
                Node successorsPredecessor = successorsFingerTable.getPredecessor();
                if (!successorsPredecessor.getNodeIdentity().equals(node.getNodeIdentity())) {
                    networkStore.joinNetwork(successorsPredecessor.getNodeIdentity().getSocketAddress());
                    Optional<Node> thisNode = successorsFingerTable.containsSuccessor(node.getNodeIdentity());
                    if(thisNode.isPresent()) {
                        successor.setType(PeerType.INCOMING);
                    } else {
                        successor.shutdown();
                    }
                }
            } catch (Exception e) {
                LOG.error("stabilization error occurred!", e);
            }
        }
    }

    private void handleGetPredecessorRequest(GetPredecessorRequest request) {
        try {
            Node node = dhtNodeManager.getNode();
            synchronized (node) {
                request.getSourcePeer().send(new GetPredecessorResponse(request.getRequestId(),
                        node.getFingerTable().getPredecessor()).serialize());
            }
        } catch (IOException e) {
            LOG.error("failed to send GetPredecessorResponse", e);
        }
    }
}
