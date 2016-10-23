package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.network.*;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.*;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;
import com.virohtus.dht.core.network.peer.PeerType;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
                case DhtProtocol.GET_SUCCESSOR_REQUEST:
                    handleGetSuccessorRequest((GetSuccessorRequest)transportableAction);
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
            fixFingers(node);
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

    private void fixFingers(Node node) throws IOException, TimeoutException, InterruptedException, PeerNotFoundException {
        FingerTable fingerTable = node.getFingerTable();
        Peer immediateSuccessor = peerStore.getPeer(fingerTable.getImmediateSuccessor());
        List<Node> newSuccessors = new ArrayList<>();
        newSuccessors.add(fingerTable.getImmediateSuccessor());
        int fingerIndex = 0;
        GetSuccessorResponse response;
        do {
            fingerIndex++;
            int distance = (int)Math.pow(2, fingerIndex);
            response = immediateSuccessor.sendRequest(new GetSuccessorRequest(distance, node),
                    GetSuccessorResponse.class).get();
            if(response.hasAnswer()) {
                newSuccessors.add(response.getNode());
            }
        } while(response.hasAnswer());
        nodeManager.setSuccessors(newSuccessors);
    }

    private void handleSetPredecessor(SetPredecessor setPredecessor) {
        nodeManager.setPredecessor(setPredecessor.getNode());
    }


    private void handleGetSuccessorRequest(GetSuccessorRequest request) {
        Node node = nodeManager.getCurrentNode();
        try {
            getOrForward(node, request);
        } catch (Exception e) {
            LOG.error("error when handling GetSuccessorRequest!", e);
        }
    }

    private void getOrForward(Node node, GetSuccessorRequest request) throws IOException, PeerNotFoundException, TimeoutException, InterruptedException {
        Peer sender = request.getSourcePeer();
        if(!node.getFingerTable().hasSuccessors()) {
            sender.send(new GetSuccessorResponse(request.getRequestId(), null).serialize());
            return;
        }
        if(request.getDistance() <= 1) {
            sender.send(new GetSuccessorResponse(request.getRequestId(), node).serialize());
            return;
        }
        int closestSuccessorIndex = getClosestSuccessorIndex(node, request.getDistance());
        int numNodesSkipped = (int)Math.pow(2, closestSuccessorIndex);
        int remainingDistance = request.getDistance() - numNodesSkipped;

        // forward request
        Node nextNode = node.getFingerTable().getSuccessorAt(closestSuccessorIndex);
        if(!forwardingInBounds(node, request.getOriginatingNode().getKeyspace(), nextNode)) {
            sender.send(new GetSuccessorResponse(request.getRequestId(), null).serialize());
            return;
        }
        Peer peer = peerStore.getPeer(node.getFingerTable().getSuccessorAt(closestSuccessorIndex));
        GetSuccessorResponse response = peer.sendRequest(
                new GetSuccessorRequest(remainingDistance, request.getOriginatingNode()),
                GetSuccessorResponse.class).get();
        sender.send(new GetSuccessorResponse(request.getRequestId(), response.getNode()).serialize());
    }

    private int getClosestSuccessorIndex(Node node, int distance) {
        int index = (int) (Math.log(distance) / Math.log(2));
        int numSuccessors = node.getFingerTable().getSuccessors().size();
        if(numSuccessors <= index) {
            // get closest one we have
            index = numSuccessors - 1;
        }
        return index;
    }

    private boolean forwardingInBounds(Node node, Keyspace originatingKeyspace, Node nextNode) {
        // todo figure this out
        int thisOffset = node.getKeyspace().getOffset();
        int nextOffset = nextNode.getKeyspace().getOffset();
        int originalOffset = originatingKeyspace.getOffset();
        if(thisOffset == nextOffset) {
            return false;
        }
        if(thisOffset > nextOffset) {
            // we have wrapped in between this node and the next
            return (thisOffset > originalOffset && nextOffset < originalOffset);
        } else {
            return (thisOffset > originalOffset && nextOffset > originalOffset) ||
                    (thisOffset < originalOffset && nextOffset < originalOffset);
        }
    }
}
