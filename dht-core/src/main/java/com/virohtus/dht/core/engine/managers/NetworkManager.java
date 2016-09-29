package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.key.Keyspace;
import com.virohtus.dht.core.key.KeyspaceService;
import com.virohtus.dht.core.network.NodeLocalNet;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.network.NodeResponsibility;
import com.virohtus.dht.core.network.event.*;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class NetworkManager implements Manager {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManager.class);
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final DhtNode dhtNode;
    private final PeerManager peerManager;
    private final RequestManager requestManager;
    private final KeyspaceService keyspaceService;

    public NetworkManager(Dispatcher dispatcher, ExecutorService executorService, DhtNode dhtNode,
                          PeerManager peerManager, RequestManager requestManager) {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        this.dhtNode = dhtNode;
        this.peerManager = peerManager;
        this.requestManager = requestManager;
        this.keyspaceService = new KeyspaceService();
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.JOIN_NETWORK_REQUEST:
                handleJoinNetworkRequest(peerId, (JoinNetworkRequest)event);
                break;
        }
    }

    public Peer openConnection(ConnectionInfo connectionInfo) throws IOException {
        Socket socket = new Socket(connectionInfo.getHost(), connectionInfo.getPort());
        Peer peer = new Peer(dispatcher, executorService, PeerType.OUTGOING, socket);
        dispatcher.dispatch(peer.getPeerId(), new PeerConnected(peer));
        return peer;
    }

    public void joinNetwork(ConnectionInfo connectionInfo) throws IOException {
        Peer peer = openConnection(connectionInfo);
        NodeLocalNet localNet = dhtNode.getLocalNet();
        JoinNetworkResponse response;
        try {
            response = requestManager.submitRequest(peer, new JoinNetworkRequest(dhtNode.getNodeIdentity()), JoinNetworkResponse.class);
            NodeLocalNet successorNet = response.getSuccessorNet();
            // set my keyspace to be what successor calculated it to be
            localNet.getResponsibility().setKeyspace(successorNet.getPredecessorResponsibility().getKeyspace());
            LOG.info("my responsibility: " + localNet.getResponsibility());
        } catch (InterruptedException e) {
            peer.shutdown();
            throw new IOException("timed out waiting for JoinNetworkResponse " + e.getMessage());
        }
    }




    private void handleJoinNetworkRequest(String peerId, JoinNetworkRequest request) {
        NodeLocalNet localNet = dhtNode.getLocalNet();
        try {
            Peer peer = peerManager.getPeer(peerId);
            Keyspace[] splitKeyspaces = keyspaceService.splitKeyspaceEqually(localNet.getResponsibility().getKeyspace());
            localNet.setPredecessorResponsibility(new NodeResponsibility(peer.getNodeIdentity(), splitKeyspaces[0]));
            localNet.getResponsibility().setKeyspace(splitKeyspaces[1]);

            if(!localNet.hasSuccessor()) {
                // set the new node to be successor as well - to complete ring
                openConnection(localNet.getPredecessorResponsibility().getNodeIdentity().getConnectionInfo());
                localNet.setSuccessorResponsibility(localNet.getPredecessorResponsibility());
                peer.send(new JoinNetworkResponse(request.getRequestId(), localNet));
            }

            LOG.info("my responsibility: " + localNet.getResponsibility());

        } catch (PeerNotFoundException e) {
            LOG.warn("received JoinNetworkRequest from unmanaged peer! " + peerId);
        } catch (InterruptedException e) {
            LOG.warn("wait for NodeIdentity interrupted during JoinNetworkRequest peerId: " + peerId);
        } catch (IOException e) {
            LOG.warn("could not establish outgoing connection to complete dht ring! " + e.getMessage());
        }
    }
}
