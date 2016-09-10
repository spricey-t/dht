package com.virohtus.dht.handler;

import com.virohtus.dht.connection.event.ConnectionDetailsRequest;
import com.virohtus.dht.connection.event.ConnectionDetailsResponse;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.NodeDelegate;
import com.virohtus.dht.node.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoreNodeDelegate implements NodeDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNodeDelegate.class);
    private final Node node;

    public CoreNodeDelegate(Node node) {
        this.node = node;
    }

    @Override
    public void peerConnected(Peer peer) {

    }

    @Override
    public void peerEventReceived(Peer peer, Event event) {
        switch (event.getType()) {
            case EventProtocol.CONNECTION_DETAILS_REQUEST:
                handleConnectionDetailsRequest(peer, (ConnectionDetailsRequest) event);
                break;
        }
    }

    @Override
    public void peerDisconnected(Peer peer) {

    }


    private void handleConnectionDetailsRequest(Peer peer, ConnectionDetailsRequest request) {
        try {
            peer.send(new ConnectionDetailsResponse(node.getConnectionDetails()));
        } catch (IOException e) {
            LOG.error("failed to send connection details response: " + e.getMessage());
        }
    }
}
