package com.virohtus.dht.core.engine.store.peer;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityRequest;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityResponse;
import com.virohtus.dht.core.engine.action.peer.PeerConnected;
import com.virohtus.dht.core.engine.action.peer.PeerDisconnected;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.peer.*;
import com.virohtus.dht.core.transport.connection.AsyncConnection;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class PeerStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PeerStore.class);
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final PeerManager peerManager;
    private final AtomicBoolean readyForShutdown;

    public PeerStore(Dispatcher dispatcher, ExecutorService executorService) {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        this.peerManager = new PeerManagerImpl();
        readyForShutdown = new AtomicBoolean(true);
    }

    public void addPeer(Peer peer) {
        peerManager.addPeer(peer);
        synchronized (readyForShutdown) {
            readyForShutdown.set(false);
        }
    }

    public Peer getPeer(Node node) throws PeerNotFoundException {
        return peerManager.getPeer(node.getNodeIdentity());
    }

    public Peer getPeer(String peerId) throws PeerNotFoundException {
        return peerManager.getPeer(peerId);
    }

    public void removePeer(Peer peer) {
        peerManager.removePeer(peer);
        if(peerManager.isEmpty()) {
            synchronized (readyForShutdown) {
                readyForShutdown.set(true);
                readyForShutdown.notifyAll();
            }
        }
    }

    public Set<Peer> listPeers() {
        return peerManager.getAllPeers();
    }

    public Set<Peer> clearPeers() {
        return peerManager.clear();
    }

    public Peer createPeer(SocketAddress socketAddress) throws IOException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(socketAddress);
        return createPeer(new AsyncConnection(executorService, socketChannel), PeerType.OUTGOING);
    }

    public Peer createPeer(Connection connection, PeerType peerType) {
        Peer peer = new Peer(dispatcher, executorService, peerType, connection);
        addPeer(peer);
        peer.listen();
        try {
//            peer.send(new GetNodeIdentityRequest().serialize());
            NodeIdentity peerIdentity = peer.sendRequest(new GetNodeIdentityRequest(),
                    GetNodeIdentityResponse.class).get().getNodeIdentity();
            peer.setNodeIdentity(peerIdentity);
        } catch (Exception e) {
            LOG.error("failed to send GetNodeIdentityRequest");
        }
        dispatcher.dispatch(new PeerConnected(peer));
        return peer;
    }

    public void shutdown() {
        synchronized (readyForShutdown) {
            listPeers().forEach(Peer::shutdown);
            while(!readyForShutdown.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    readyForShutdown.wait();
                } catch (InterruptedException e) {
                    LOG.warn("wait for PeerStore shutdown interrupted!");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onAction(Action action) {
        if(action instanceof PeerDisconnected) {
            removePeer(((PeerDisconnected)action).getPeer());
        }

        if(action instanceof TransportableAction) {
            TransportableAction transportableAction = (TransportableAction)action;
            switch (transportableAction.getType()) {
                case DhtProtocol.GET_NODE_IDENTITY_RESPONSE:
                    handleGetNodeIdentityResponse((GetNodeIdentityResponse)transportableAction);
                    break;
            }
        }
    }

    private void handleGetNodeIdentityResponse(GetNodeIdentityResponse response) {
        try {
            Peer peer = getPeer(response.getSourcePeer().getId());
            peer.setNodeIdentity(response.getNodeIdentity());
        } catch (PeerNotFoundException e) {
            LOG.warn("received GetNodeIdentityResponse for untracked peer! " + response.getSourcePeer().getId());
        }
    }
}
