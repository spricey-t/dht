package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityRequest;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityResponse;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NodeIdentityStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(NodeIdentityStore.class);
    private final DhtNode dhtNode;

    public NodeIdentityStore(DhtNode dhtNode) {
        this.dhtNode = dhtNode;
    }

    @Override
    public void onAction(Action action) {
        if(action instanceof GetNodeIdentityRequest) {
            handleGetNodeIdentityRequest((GetNodeIdentityRequest)action);
            return;
        }
    }

    private void handleGetNodeIdentityRequest(GetNodeIdentityRequest request) {
        if(!request.hasSourcePeer()) {
            LOG.error("received GetnodeIdentityRequest from null peer!");
            return;
        }
        try {
            GetNodeIdentityResponse response = new GetNodeIdentityResponse(dhtNode.getNodeIdentity());
            request.getSourcePeer().send(new DhtEvent(response.serialize()));
        } catch (IOException e) {
            LOG.error("failed to send GetNodeIdentityResponse to peer: " + request.getSourcePeer().getId());
        }
    }
}
