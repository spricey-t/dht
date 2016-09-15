package com.virohtus.dht.core;

import com.virohtus.dht.core.engine.DhtManager;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.handler.LoggingHandler;
import com.virohtus.dht.core.network.FingerTable;
import com.virohtus.dht.core.peer.PeerPool;
import com.virohtus.dht.core.peer.handler.PeerPoolHandler;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.TCPServer;
import com.virohtus.dht.core.transport.server.event.ServerShutdown;
import com.virohtus.dht.core.transport.server.event.ServerStart;
import com.virohtus.dht.core.transport.server.handler.SocketConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StabilizingDhtNode implements DhtNode {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);
    private static final int SHUTDOWN_TIMEOUT = 5;

    private final ExecutorService executorService;
    private final HandlerChain handlerChain;
    private final DhtManager dhtManager;
    private final PeerPool peerPool;
    private final Server server;

    public StabilizingDhtNode() {
        executorService = Executors.newCachedThreadPool();
        handlerChain = new HandlerChain();
        dhtManager = new DhtManager(handlerChain, executorService);
        peerPool = new PeerPool();
        server = new TCPServer(handlerChain, executorService);

        handlerChain.addHandler(new LoggingHandler());
        handlerChain.addHandler(new SocketConnectionHandler(handlerChain, executorService));
        handlerChain.addHandler(new PeerPoolHandler(peerPool));
        handlerChain.addHandler(dhtManager);
    }

    @Override
    public void start(int serverPort) throws IOException {
        server.start(serverPort);
        handlerChain.handle(new ServerStart(server.getConnectionInfo().getPort()));
    }

    @Override
    public void shutdown() {
        server.shutdown();

        handlerChain.handle(new ServerShutdown());

        executorService.shutdown();
        try {
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    @Override
    public void joinNetwork(ConnectionInfo existingNode) throws IOException {
        dhtManager.join(existingNode);
    }

    @Override
    public void leaveNetwork() {

    }

    @Override
    public void registerEventHandler(EventHandler handler) {

    }

    @Override
    public void unregisterEventHandler(EventHandler handler) {

    }

    @Override
    public String getNodeId() {
        return null;
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return null;
    }

    @Override
    public FingerTable getFingerTable() {
        return null;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        StabilizingDhtNode node = new StabilizingDhtNode();
        node.start(11081);
//        node.start(11082);
//        node.joinNetwork(new ConnectionInfo("localhost", 11081));
        Thread.sleep(10000);
        node.shutdown();
    }
}
