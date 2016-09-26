package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.event.NodeIdentityRequest;
import com.virohtus.dht.core.network.event.NodeIdentityResponse;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class NetworkManager implements Manager {

    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final RequestManager requestManager;

    public NetworkManager(Dispatcher dispatcher, ExecutorService executorService, RequestManager requestManager) {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        this.requestManager = requestManager;
    }

    @Override
    public void handle(String peerId, Event event) {
    }

    public void joinNetwork(ConnectionInfo connectionInfo) throws IOException {
        Socket socket = new Socket(connectionInfo.getHost(), connectionInfo.getPort());
        Peer peer = new Peer(dispatcher, executorService, PeerType.OUTGOING, socket);
        dispatcher.dispatch(peer.getPeerId(), new PeerConnected(peer));

        /*
        try {
            NodeIdentityResponse response = requestManager.submitRequest(peer, new NodeIdentityRequest(), NodeIdentityResponse.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

}
