package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.PeerConnected;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.transport.connection.AsyncConnection;
import com.virohtus.dht.core.transport.connection.Connection;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class PeerStore implements Store {

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
    }
}
