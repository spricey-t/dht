package com.virohtus.dht.core;

import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.SingleThreadedDispatcher;
import com.virohtus.dht.core.engine.store.LogStore;
import com.virohtus.dht.core.engine.store.network.NetworkStore;
import com.virohtus.dht.core.engine.store.network.StabilizationStore;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.engine.store.server.ServerStore;
import com.virohtus.dht.core.network.*;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;
import com.virohtus.dht.core.util.IdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.concurrent.*;

public class StabilizingDhtNodeManager implements DhtNodeManager {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNodeManager.class);
    private static final int SHUTDOWN_TIMEOUT = 3; // seconds
    private final NodeManager nodeManager;
    private final ExecutorService executorService;
    private final Dispatcher dispatcher;
    private final ServerStore serverStore;
    private final PeerStore peerStore;
    private final NetworkStore networkStore;
    private final StabilizationStore stabilizationStore;

    public StabilizingDhtNodeManager(int serverPort) throws IOException {
        Node node = new Node(new NodeIdentity(new IdService().generateId(), null), new Keyspace(), new FingerTable());
        nodeManager = new NodeManager(node);
        executorService = Executors.newCachedThreadPool();
        dispatcher = new SingleThreadedDispatcher(executorService);

        peerStore = new PeerStore(dispatcher, executorService);
        serverStore = new ServerStore(dispatcher, executorService, peerStore, new InetSocketAddress(serverPort));
        networkStore = new NetworkStore(this, nodeManager, peerStore);
        stabilizationStore = new StabilizationStore(executorService, nodeManager, peerStore);

        dispatcher.registerStore(new LogStore());
        dispatcher.registerStore(peerStore);
        dispatcher.registerStore(serverStore);
        dispatcher.registerStore(networkStore);
        dispatcher.registerStore(stabilizationStore);
    }

    @Override
    public void start() throws ExecutionException, InterruptedException, IOException {
        serverStore.start();
        nodeManager.setSocketAddress(serverStore.getSocketAddress());
        stabilizationStore.start();
        dispatcher.start();
    }

    @Override
    public void shutdown() {
        serverStore.shutdown();
        peerStore.shutdown();
        dispatcher.shutdown();
        stabilizationStore.shutdown();
        executorService.shutdown();

        try {
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("reached shutdown timeout. forcing shutdown...");
            executorService.shutdownNow();
        }
    }

    @Override
    public boolean isShutdown() {
        return !serverStore.isAlive();
    }

    @Override
    public void joinNetwork(SocketAddress socketAddress) throws IOException, InterruptedException, TimeoutException {
        networkStore.join(socketAddress);
    }

    @Override
    public Node getNode() {
        return nodeManager.getCurrentNode();
    }

    @Override
    public Network getNetwork() throws InterruptedException, TimeoutException, PeerNotFoundException, IOException {
        return networkStore.getNetwork();
    }

    private void connect(SocketAddress socketAddress) throws IOException, TimeoutException, InterruptedException {
        Peer peer = peerStore.createPeer(socketAddress);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        StabilizingDhtNodeManager node = new StabilizingDhtNodeManager(0);
        try {
            node.start();

            Scanner key = new Scanner(System.in);
            String cmd = "";
            while (!cmd.equalsIgnoreCase("quit")) {
                cmd = key.nextLine();
                String[] cmdArgs = cmd.split("\\s");
                if (cmdArgs[0].equals("connect")) {
                    node.joinNetwork(new InetSocketAddress(cmdArgs[1], Integer.parseInt(cmdArgs[2])));
//                    node.connect(new InetSocketAddress(cmdArgs[1], Integer.parseInt(cmdArgs[2])));
                }
            }
        } finally {
            node.shutdown();
        }
    }
}
