package com.virohtus.dht.core;

import com.virohtus.dht.core.engine.DhtManager;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.handler.LoggingHandler;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerNotFoundException;
import com.virohtus.dht.core.peer.PeerPool;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.handler.PeerPoolHandler;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.TCPServer;
import com.virohtus.dht.core.transport.server.event.ServerShutdown;
import com.virohtus.dht.core.transport.server.event.ServerStart;
import com.virohtus.dht.core.transport.server.handler.SocketConnectionHandler;
import com.virohtus.dht.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StabilizingDhtNode implements DhtNode {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);
    private static final int SHUTDOWN_TIMEOUT = 5;

    private final ExecutorService executorService;
    private final HandlerChain handlerChain;
    private final PeerPool peerPool;
    private final NodeNetwork nodeNetwork;
    private final DhtManager dhtManager;
    private final Server server;

    private final String id;

    public StabilizingDhtNode() {
        executorService = Executors.newCachedThreadPool();
        handlerChain = new HandlerChain();
        peerPool = new PeerPool();
        nodeNetwork = new NodeNetwork();
        dhtManager = new DhtManager(handlerChain, executorService, this);
        server = new TCPServer(handlerChain, executorService);

        handlerChain.addHandler(new SocketConnectionHandler(handlerChain, executorService));
        handlerChain.addHandler(new PeerPoolHandler(peerPool));
        handlerChain.addHandler(dhtManager);
        handlerChain.addHandler(new LoggingHandler());

        id = new IdUtil().generateId();
    }

    @Override
    public void start(int serverPort) throws IOException {
        server.start(serverPort);
        handlerChain.handle(null, new ServerStart(server.getConnectionInfo().getPort()));
    }

    @Override
    public void shutdown() {
        server.shutdown();

        handlerChain.handle(null, new ServerShutdown());

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
    public Peer openConnection(ConnectionInfo connectionInfo) throws IOException {
        return new Peer(handlerChain, executorService, PeerType.OUTGOING,
                new Socket(connectionInfo.getHost(), connectionInfo.getPort()));
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
        return id;
    }

    @Override
    public Peer getPeer(String peerId) throws PeerNotFoundException {
        return peerPool.getPeer(peerId);
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return server.getConnectionInfo();
    }

    @Override
    public NodeIdentity getNodeIdentity() {
        return new NodeIdentity(getNodeId(), getConnectionInfo());
    }

    @Override
    public NodeNetwork getNodeNetwork() {
        return nodeNetwork;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        StabilizingDhtNode node = new StabilizingDhtNode();
        node.start(0);

        String cmd = "";
        Scanner keyboard = new Scanner(System.in);
        while(!cmd.equals("quit")) {
            cmd = keyboard.nextLine();
            String[] cmdArgs = cmd.split("\\s");
            switch (cmdArgs[0]) {
                case "connect":
                    node.joinNetwork(new ConnectionInfo(cmdArgs[1], Integer.parseInt(cmdArgs[2])));
                    break;
            }
        }

        node.shutdown();
    }
}
