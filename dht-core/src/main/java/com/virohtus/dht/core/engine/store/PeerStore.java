package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.PeerConnected;
import com.virohtus.dht.core.engine.action.ServerShutdown;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.transport.connection.AsyncConnection;
import com.virohtus.dht.core.transport.connection.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class PeerStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PeerStore.class);
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final Map<String, Peer> peers;

    public PeerStore(Dispatcher dispatcher, ExecutorService executorService) {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        peers = new HashMap<>();
    }

    public void addPeer(Peer peer) {
        synchronized (peers) {
            peers.put(peer.getId(), peer);
        }
    }

    public Peer getPeer(String peerId) throws PeerNotFoundException {
        synchronized (peers) {
            if(peers.containsKey(peerId)) {
                return peers.get(peerId);
            }
            throw new PeerNotFoundException();
        }
    }

    public Peer removePeer(Peer peer) {
        synchronized (peers) {
            return peers.remove(peer);
        }
    }

    public Set<Peer> listPeers() {
        synchronized (peers) {
            return new HashSet<>(peers.values());
        }
    }

    public Set<Peer> clearPeers() {
        synchronized (peers) {
            Set<Peer> peerset = listPeers();
            peers.clear();
            return peerset;
        }
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
        dispatcher.dispatch(new PeerConnected(peer));
        return peer;
    }

    @Override
    public void onAction(Action action) {
        if(action instanceof ServerShutdown) {
            handleServerShutdown((ServerShutdown)action);
        }
    }

    private void handleServerShutdown(ServerShutdown serverShutdown) {
        LOG.info("closing down all connections");
        clearPeers().forEach(Peer::shutdown);
    }
}
