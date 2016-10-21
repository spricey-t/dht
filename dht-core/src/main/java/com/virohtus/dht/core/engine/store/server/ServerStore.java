package com.virohtus.dht.core.engine.store.server;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityRequest;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityResponse;
import com.virohtus.dht.core.engine.action.server.ServerShutdown;
import com.virohtus.dht.core.engine.action.server.ServerStarted;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerType;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.server.AsyncServer;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerStore implements Store, ServerDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStore.class);
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final Server server;
    private final PeerStore peerStore;
    private final AtomicBoolean shutdownLock;

    public ServerStore(Dispatcher dispatcher, ExecutorService executorService,
                       PeerStore peerStore, SocketAddress socketAddress) throws IOException {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        this.peerStore = peerStore;
        this.shutdownLock = new AtomicBoolean(false);
        server = new AsyncServer(this, executorService, socketAddress);
    }

    public void start() {
        server.listen();
        synchronized (shutdownLock) {
            shutdownLock.set(false);
        }
        dispatcher.dispatch(new ServerStarted(server));
    }

    public void shutdown() {
        server.shutdown();
        synchronized (shutdownLock) {
            while(!shutdownLock.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    shutdownLock.wait();
                } catch (InterruptedException e) {
                    LOG.warn("wait for graceful shutdown interrupted!");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public SocketAddress getSocketAddress() throws IOException {
        return server.getSocketAddress();
    }

    public boolean isAlive() {
        return server.isListening();
    }

    @Override
    public void onAction(Action action) {
    }

    @Override
    public void connectionOpened(Connection connection) {
        Peer peer = peerStore.createPeer(connection, PeerType.INCOMING);
    }

    @Override
    public void serverShutdown() {
        dispatcher.dispatch(new ServerShutdown());
        synchronized (shutdownLock) {
            shutdownLock.set(true);
            shutdownLock.notifyAll();
        }
    }
}
