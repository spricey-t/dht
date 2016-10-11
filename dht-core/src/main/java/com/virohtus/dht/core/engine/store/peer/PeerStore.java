package com.virohtus.dht.core.engine.store.peer;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.peer.PeerConnected;
import com.virohtus.dht.core.engine.action.peer.PeerDisconnected;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;
import com.virohtus.dht.core.network.peer.PeerType;
import com.virohtus.dht.core.transport.connection.AsyncConnection;
import com.virohtus.dht.core.transport.connection.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class PeerStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(PeerStore.class);
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final Map<String, Peer> peers;
    private final AtomicBoolean readyForShutdown;

    public PeerStore(Dispatcher dispatcher, ExecutorService executorService) {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        peers = new HashMap<>();
        readyForShutdown = new AtomicBoolean(true);
    }

    public void addPeer(Peer peer) {
        synchronized (peers) {
            peers.put(peer.getId(), peer);
        }
        synchronized (readyForShutdown) {
            readyForShutdown.set(false);
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

    public void removePeer(Peer peer) {
        synchronized (peers) {
            if(!peers.containsKey(peer.getId())) {
                return;
            }
            peers.remove(peer.getId());
            if(peers.isEmpty()) {
                synchronized (readyForShutdown) {
                    readyForShutdown.set(true);
                    readyForShutdown.notifyAll();
                }
            }
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
    }
}
