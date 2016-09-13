package com.virohtus.dht.handler;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.NodeDelegate;
import com.virohtus.dht.node.Peer;
import com.virohtus.dht.node.PeerDetails;
import com.virohtus.dht.node.event.*;
import com.virohtus.dht.node.overlay.Finger;
import com.virohtus.dht.node.overlay.FingerTable;
import com.virohtus.dht.node.overlay.OverlayNode;
import com.virohtus.dht.utils.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class CoreNodeDelegate implements NodeDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNodeDelegate.class);
    private final Node node;
    private final ExecutorService executorService;

    private final Resolvable<GetOverlay> getOverlayResolvable = new Resolvable<>();
    private final Resolvable<FingerTableResponse> fingerTableResponseResolvable = new Resolvable<>();

    public CoreNodeDelegate(Node node, ExecutorService executorService) {
        this.node = node;
        this.executorService = executorService;
    }

    @Override
    public void peerConnected(Peer peer) {

    }

    @Override
    public void connectedToPeer(Peer peer) {
        try {
            peer.send(new FingerTableRequest(node.getId()));
            FingerTableResponse response = fingerTableResponseResolvable.get();
            Finger peerFinger = new Finger(
                peer.getId(),
                peer.getPeerNodeId(node.getId()),
                peer.getConnectionDetails(node.getId())
            );

            // add peer as successor
            List<Finger> successors = new ArrayList<>();
            successors.add(peerFinger);

            // calculate predecessor
            Finger predecessor = (response.getFingerTable().getPredecessor() == null) ?
                peerFinger :
                response.getFingerTable().getPredecessor();

            // calculate the rest of the successors
            // todo

            // update node's fingertable
            node.setFingerTable(new FingerTable(predecessor, successors));

            // begin telling predecessors to update finger tables
            // todo

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
}
