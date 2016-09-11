package com.virohtus.dht.handler;

import com.virohtus.dht.connection.event.ConnectionDetailsRequest;
import com.virohtus.dht.connection.event.ConnectionDetailsResponse;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.NodeDelegate;
import com.virohtus.dht.node.Peer;
import com.virohtus.dht.node.PeerDetails;
import com.virohtus.dht.node.event.GetOverlay;
import com.virohtus.dht.node.event.PeerDetailsRequest;
import com.virohtus.dht.node.event.PeerDetailsResponse;
import com.virohtus.dht.node.overlay.OverlayNode;
import com.virohtus.dht.utils.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class CoreNodeDelegate implements NodeDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNodeDelegate.class);
    private final Node node;
    private final ExecutorService executorService;

    private final Resolvable<GetOverlay> getOverlayResolvable = new Resolvable<>();

    public CoreNodeDelegate(Node node, ExecutorService executorService) {
        this.node = node;
        this.executorService = executorService;
    }

    @Override
    public void peerConnected(Peer peer) {

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
        if(node.listPeers().isEmpty()) {
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
