package com.virohtus.dht.handler;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.*;
import com.virohtus.dht.node.event.*;
import com.virohtus.dht.node.overlay.Finger;
import com.virohtus.dht.node.overlay.FingerTable;
import com.virohtus.dht.node.overlay.OverlayNode;
import com.virohtus.dht.utils.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class CoreNodeDelegate implements NodeDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNodeDelegate.class);
    private static final int STABILIZE_PERIOD = 10; //todo externalize this
    private final Node node;
    private final ExecutorService executorService;
    private final ScheduledExecutorService stabilizerExecutorService;

    private final Resolvable<GetOverlay> getOverlayResolvable = new Resolvable<>();
    private final Resolvable<GetPredecessorResponse> getPredecessorResponseResolvable = new Resolvable<>();

    public CoreNodeDelegate(Node node, ExecutorService executorService) {
        this.node = node;
        this.executorService = executorService;
        this.stabilizerExecutorService = Executors.newSingleThreadScheduledExecutor();
        startStabilizer();
    }

    @Override
    public void peerConnected(Peer peer) {

    }

    @Override
    public void onNetworkJoin(Peer peer) {
        try {
            Finger peerFinger = new Finger(
                peer.getId(),
                peer.getPeerNodeId(node.getId()),
                peer.getConnectionDetails(node.getId())
            );

            // notify peer that this is the new predecessor
            peer.send(new SetPredecessorRequest(node.getId()));

            // add peer as successor
            List<Finger> successors = new ArrayList<>();
            successors.add(peerFinger);

            // update node's fingertable
            node.setFingerTable(new FingerTable(null, successors));
        } catch (Exception e) {
            LOG.error("finger table creation failed when connecting to peer: " + peer.getId() + " " + e.getMessage());
        }
    }

    @Override
    public void peerEventReceived(Peer peer, Event event) {
        switch (event.getType()) {
            case EventProtocol.PEER_DETAILS_REQUEST:
                handlePeerDetailsRequest(peer, (PeerDetailsRequest) event);
                break;
            case EventProtocol.GET_OVERLAY:
                handleGetOverlay(peer, (GetOverlay) event);
                break;
            case EventProtocol.GET_PREDECESSOR_REQUEST:
                handleGetPredecessorRequest(peer, (GetPredecessorRequest) event);
                break;
            case EventProtocol.GET_PREDECESSOR_RESPONSE:
                handleGetPredecessorResponse(peer, (GetPredecessorResponse) event);
                break;
            case EventProtocol.SET_PREDECESSOR_REQUEST:
                handleSetPredecessorRequest(peer, (SetPredecessorRequest) event);
                break;
        }
    }

    @Override
    public void peerDisconnected(Peer peer) {

    }



    public List<OverlayNode> getOverlay() throws IOException, InterruptedException {
        GetOverlay getOverlay = new GetOverlay(node.getOverlayNode());
        if(node.listSuccessors().isEmpty()) {
            return getOverlay.getOverlayNodes();
        }
        Peer successor = node.getSuccessor(1);
        successor.send(getOverlay);
        return getOverlayResolvable.get().getOverlayNodes();
    }



    private void handlePeerDetailsRequest(Peer peer, PeerDetailsRequest request) {
        try {
            PeerDetails peerDetails = new PeerDetails(node.getId(), node.getConnectionDetails());
            peer.send(new PeerDetailsResponse(node.getId(), peerDetails));
        } catch (IOException e) {
            LOG.error("failed to send peer details response: " + e.getMessage());
        }
    }

    private void handleGetOverlay(Peer peer, GetOverlay getOverlay) {
        if(getOverlay.getInitiatingNodeId().equals(node.getId())) {
            getOverlayResolvable.resolve(getOverlay);
            return;
        }

        // pass it along
        if(node.listPeers().size() > 0)  {
            Peer successor = node.getSuccessor(1);
            try {
                getOverlay.addOverlayNode(node.getOverlayNode());
                successor.send(getOverlay);
            } catch (IOException e) {
                LOG.error("failed to send GetOverlay to peer: " + successor.getId());
            }
        } else {
            LOG.warn("received GetOverlay, but we have no where to go! (is the overlay not circular?)");
        }
    }

    private Future startStabilizer() {
        return stabilizerExecutorService.scheduleAtFixedRate(() -> {
            FingerTable fingerTable = node.getFingerTable();
            if(fingerTable.getSuccessors().isEmpty()) {
                return;
            }
            Finger finger = node.getFingerTable().getSuccessors().get(0);
            try {
                Peer peer = node.getPeer(finger.getPeerId());
                peer.send(new GetPredecessorRequest(node.getId()));
                GetPredecessorResponse response = getPredecessorResponseResolvable.get();
                Finger successorsPredecessor = response.getPredecessor();
                if(!successorsPredecessor.getPeerNodeId().equals(node.getId())) {
                    Peer newSuccessor = node.connectToPeer(successorsPredecessor.getConnectionDetails());
                    node.setFingerTable(new FingerTable(fingerTable.getPredecessor(), Arrays.asList(successorsPredecessor)));
                    newSuccessor.send(new SetPredecessorRequest(node.getId()));
                }
                LOG.info("ran stabilization");
            } catch (PeerNotFoundException e) {
                LOG.error("somehow a finger maintained a peerId for a nonexistent peer.");
            } catch (IOException e) {
                LOG.error("stabilization failed: " + e.getMessage());
            } catch (InterruptedException e) {
                LOG.warn("wait for GetPredecessorResponse interrupted" + e.getMessage());
            }
        }, 0, STABILIZE_PERIOD, TimeUnit.SECONDS);
    }

    private void handleGetPredecessorRequest(Peer peer, GetPredecessorRequest request) {
        try {
            peer.send(new GetPredecessorResponse(node.getId(), node.getFingerTable().getPredecessor()));
        } catch (IOException e) {
            LOG.error("failed to send GetPredecessorResponse " + e.getMessage());
        }
    }

    private void handleGetPredecessorResponse(Peer peer, GetPredecessorResponse response) {
        getPredecessorResponseResolvable.resolve(response);
    }

    private void handleSetPredecessorRequest(Peer peer, SetPredecessorRequest request) {
        try {
            node.getFingerTable().setPredecessor(new Finger(
                    peer.getId(),
                    peer.getPeerNodeId(node.getId()),
                    peer.getConnectionDetails(node.getId())
            ));
        } catch (Exception e) {
            LOG.error("failed to set predecessor! " + e.getMessage());
        }
    }

}
