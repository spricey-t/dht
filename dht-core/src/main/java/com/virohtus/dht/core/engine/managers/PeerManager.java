package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerPool;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import com.virohtus.dht.core.transport.server.event.SocketConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class PeerManager implements Manager {

    private static final Logger LOG = LoggerFactory.getLogger(PeerManager.class);
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final PeerPool peerPool;
    private final Object shutdownLock;

    public PeerManager(Dispatcher dispatcher, ExecutorService executorService) {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        peerPool = new PeerPool();
        shutdownLock = new Object();
    }

    public Peer getPeer(String peerId) throws PeerNotFoundException {
        return peerPool.getPeer(peerId);
    }

    public Set<Peer> listPeers() {
        return peerPool.listPeers();
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.SOCKET_CONNECT:
                handleSocketConnect((SocketConnect)event);
                break;
            case DhtProtocol.PEER_CONNECTED:
                handlePeerConnected(peerId, (PeerConnected)event);
                break;
            case DhtProtocol.PEER_DISCONNECTED:
                handlePeerDisconnected(peerId, (PeerDisconnected)event);
                break;
            case DhtProtocol.SERVER_SHUTDOWN:
                handleServerShutdown();
                break;
        }
    }

    private void handleSocketConnect(SocketConnect socketConnect) {
        try {
            Peer peer = new Peer(dispatcher, executorService, PeerType.INCOMING, socketConnect.getSocket());
            dispatcher.dispatch(peer.getPeerId(), new PeerConnected(peer));
        } catch (IOException e) {
            LOG.warn("could not create peer from socket: " + e.getMessage());
            try {
                socketConnect.getSocket().close();
            } catch (IOException e1) {
                // we don't care if we failed to flush
            }
        }
    }

    private void handlePeerConnected(String peerId, PeerConnected peerConnected) {
        peerPool.addPeer(peerConnected.getPeer());
    }

    private void handlePeerDisconnected(String peerId, PeerDisconnected peerDisconnected) {
        try {
            peerPool.removePeer(peerDisconnected.getPeer().getPeerId());
            synchronized (shutdownLock) {
                if (peerPool.isEmpty()) {
                    shutdownLock.notifyAll();
                }
            }
        } catch (PeerNotFoundException e) {
            LOG.warn("received PeerDisconnected event for untracked peer: " + peerDisconnected.getPeer());
        }
    }

    private void handleServerShutdown() {
        synchronized (shutdownLock) {
            if(peerPool.isEmpty()) {
                return;
            }
            peerPool.listPeers().forEach(Peer::shutdown);
            try {
                shutdownLock.wait();
            } catch (InterruptedException e) {
                LOG.warn("peer manager shutdown interrupted");
                Thread.currentThread().interrupt();
            }
        }
    }
}
