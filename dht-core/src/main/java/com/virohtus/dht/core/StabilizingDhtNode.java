package com.virohtus.dht.core;

import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.managers.*;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StabilizingDhtNode implements DhtNode {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);
    private static final int SHUTDOWN_TIMEOUT = 5;

    private final String id;
    private final NodeIdentity nodeIdentity;
    private final ExecutorService executorService;
    private final Dispatcher dispatcher;
    private final int serverPort;

    private final LogManager logManager;
    private final RequestManager requestManager;
    private final ServerManager serverManager;
    private final PeerManager peerManager;
    private final DhtNodeManager dhtNodeManager;
    private final NetworkManager networkManager;

    public StabilizingDhtNode(int serverPort) {
        id = new IdUtil().generateId();
        nodeIdentity = new NodeIdentity(id, null);
        executorService = Executors.newCachedThreadPool();
        dispatcher = new Dispatcher();
        this.serverPort = serverPort;

        // instantiate core managers
        logManager = new LogManager();
        requestManager = new RequestManager();
        serverManager = new ServerManager(dispatcher, executorService);
        peerManager = new PeerManager(dispatcher, executorService, requestManager);
        dhtNodeManager = new DhtNodeManager(this, peerManager);
        networkManager = new NetworkManager(dispatcher, executorService, requestManager);

        // register core managers
        dispatcher.registerManager(logManager);
        dispatcher.registerManager(requestManager);
        dispatcher.registerManager(serverManager);
        dispatcher.registerManager(peerManager);
        dispatcher.registerManager(dhtNodeManager);
        dispatcher.registerManager(networkManager);
    }

    @Override
    public void start() throws IOException {
        serverManager.start(serverPort);
        nodeIdentity.setConnectionInfo(serverManager.getConnectionInfo());
    }

    @Override
    public void shutdown() {
        serverManager.shutdown();
        executorService.shutdown();
        try {
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    @Override
    public boolean isAlive() {
        return serverManager.isAlive();
    }

    @Override
    public void joinNetwork(ConnectionInfo existingNode) throws IOException {
        networkManager.joinNetwork(existingNode);
    }

    @Override
    public Peer openConnection(ConnectionInfo connectionInfo) throws IOException {
        return null;
    }

    @Override
    public void leaveNetwork() {

    }

    @Override
    public NodeIdentity getNodeIdentity() {
        return nodeIdentity;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        StabilizingDhtNode node = new StabilizingDhtNode(0);
        node.start();

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
