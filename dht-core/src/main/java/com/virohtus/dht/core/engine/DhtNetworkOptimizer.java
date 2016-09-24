package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.peer.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DhtNetworkOptimizer implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DhtNetworkOptimizer.class);
    private final DhtNode dhtNode;

    public DhtNetworkOptimizer(DhtNode dhtNode) {
        this.dhtNode = dhtNode;
    }

    @Override
    public void handle(String peerId, Event event) {
    }

    public void sendOptimizationRequest() {
        // send in counter clockwise direction
        // prepend node at each hop
    }

    private void handleOptimizationRequest(String peerId, Event event) {
        List<NodeIdentity> identities = new ArrayList<>();
        int thisIndex = identities.indexOf(dhtNode.getNodeIdentity());
        if(thisIndex == -1) {
            LOG.error("received optimization request however this node does not exist in the network!");
            return;
        }

        for(int i = 0; i < identities.size(); i++) {
            try {
                Peer peer = dhtNode.openConnection(null);
            } catch (IOException e) {
            }
        }
        identities.add(0, dhtNode.getNodeIdentity());
    }

}
