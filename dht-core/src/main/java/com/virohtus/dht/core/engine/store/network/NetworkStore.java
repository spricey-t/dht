package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNodeManager;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityRequest;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityResponse;
import com.virohtus.dht.core.engine.action.network.JoinNetworkRequest;
import com.virohtus.dht.core.engine.action.network.JoinNetworkResponse;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.FingerTable;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

public class NetworkStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkStore.class);
    private final NodeNetwork nodeNetwork;
    private final DhtNodeManager dhtNodeManager;
    private final PeerStore peerStore;

    public NetworkStore(DhtNodeManager dhtNodeManager, PeerStore peerStore) {
        this.nodeNetwork = new NodeNetwork();
        this.dhtNodeManager = dhtNodeManager;
        this.peerStore = peerStore;
    }

    public void joinNetwork(SocketAddress socketAddress) throws IOException, InterruptedException, TimeoutException {
        Peer peer = peerStore.createPeer(socketAddress);
        JoinNetworkResponse response = peer.sendRequest(new JoinNetworkRequest(dhtNodeManager.getNode()),
                JoinNetworkResponse.class).get();
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
        if(!request.hasSourcePeer()) {
            LOG.error("received JoinNetworkRequest from null peer!");
            return;
        }
        FingerTable fingerTable = dhtNodeManager.getNode().getFingerTable();
        fingerTable.setPredecessor(request.getNode());

        if(!fingerTable.hasSuccessors()) {

        }
    }

    private void handleGetNodeIdentityRequest(GetNodeIdentityRequest request) {
        if(!request.hasSourcePeer()) {
            LOG.error("received GetNodeIdentityRequest from null peer!");
            return;
        }
        try {
            GetNodeIdentityResponse response = new GetNodeIdentityResponse(request.getRequestId(),
                    dhtNodeManager.getNode().getNodeIdentity());
            request.getSourcePeer().send(response.serialize());
        } catch (IOException e) {
            LOG.error("failed to send GetNodeIdentityResponse to peer: " + request.getSourcePeer().getId());
        }
    }
}
