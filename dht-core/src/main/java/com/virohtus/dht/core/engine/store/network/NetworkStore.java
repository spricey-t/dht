package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityRequest;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityResponse;
import com.virohtus.dht.core.engine.action.network.JoinNetworkRequest;
import com.virohtus.dht.core.engine.action.network.JoinNetworkResponse;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

public class NetworkStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkStore.class);
    private final DhtNode dhtNode;
    private final PeerStore peerStore;

    public NetworkStore(DhtNode dhtNode, PeerStore peerStore) {
        this.dhtNode = dhtNode;
        this.peerStore = peerStore;
    }

    public void joinNetwork(SocketAddress socketAddress) throws IOException, InterruptedException, TimeoutException {
        Peer peer = peerStore.createPeer(socketAddress);
        JoinNetworkResponse response = peer.sendRequest(new JoinNetworkRequest(), JoinNetworkResponse.class).get();
    }

    @Override
    public void onAction(Action action) {
        if(action instanceof TransportableAction) {
            TransportableAction transportableAction = (TransportableAction)action;
            switch (transportableAction.getType()) {
                case DhtProtocol.JOIN_NETWORK_REQUEST:
                    handleJoinNetworkRequest((JoinNetworkRequest)transportableAction);
                    break;
                case DhtProtocol.GET_NODE_IDENTITY_REQUEST:
                    handleGetNodeIdentityRequest((GetNodeIdentityRequest)transportableAction);
                    break;
            }
        }
    }

    private void handleJoinNetworkRequest(JoinNetworkRequest request) {

    }

    private void handleGetNodeIdentityRequest(GetNodeIdentityRequest request) {
        if(!request.hasSourcePeer()) {
            LOG.error("received GetnodeIdentityRequest from null peer!");
            return;
        }
        try {
            GetNodeIdentityResponse response = new GetNodeIdentityResponse(request.getRequestId(), dhtNode.getNodeIdentity());
            request.getSourcePeer().send(response.serialize());
        } catch (IOException e) {
            LOG.error("failed to send GetNodeIdentityResponse to peer: " + request.getSourcePeer().getId());
        }
    }
}
