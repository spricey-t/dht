package com.virohtus.dht.core;

import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.store.LogStore;
import com.virohtus.dht.core.engine.store.network.NodeIdentityStore;
import com.virohtus.dht.core.engine.store.server.ServerStore;
import com.virohtus.dht.core.engine.SingleThreadedDispatcher;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.util.IdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Scanner;
import java.util.concurrent.*;

public class StabilizingDhtNode implements DhtNode {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);
    private static final int SHUTDOWN_TIMEOUT = 3; // seconds
    private final String nodeId;
    private final ExecutorService executorService;
    private final Dispatcher dispatcher;
    private final ServerStore serverStore;
    private final PeerStore peerStore;

    public StabilizingDhtNode(int serverPort) throws IOException {
        nodeId = new IdService().generateId();
        executorService = Executors.newCachedThreadPool();
        dispatcher = new SingleThreadedDispatcher(executorService);

        peerStore = new PeerStore(dispatcher, executorService);
        serverStore = new ServerStore(dispatcher, executorService,
                peerStore, new InetSocketAddress("localhost", serverPort));

        dispatcher.registerStore(new LogStore());
        dispatcher.registerStore(peerStore);
        dispatcher.registerStore(serverStore);
        dispatcher.registerStore(new NodeIdentityStore(this));
    }

    @Override
    public void start() throws ExecutionException, InterruptedException, IOException {
        serverStore.start();
        dispatcher.start();
    }

    @Override
    public void shutdown() {
        serverStore.shutdown();
        peerStore.shutdown();
        dispatcher.shutdown();
        executorService.shutdown();

        try {
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("reached shutdown timeout. forcing shutdown...");
            executorService.shutdownNow();
        }
    }

    @Override
    public void joinNetwork(SocketAddress socketAddress) {

    }

    @Override
    public NodeIdentity getNodeIdentity() {
        SocketAddress socketAddress = null;
        try {
            socketAddress = serverStore.getSocketAddress();
        } catch (IOException e) {
            LOG.error("could not get socket address for dht server! " + e);
        }
        return new NodeIdentity(nodeId, socketAddress);
    }

    private void connect(SocketAddress socketAddress) throws IOException {
        peerStore.createPeer(socketAddress);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        StabilizingDhtNode node = new StabilizingDhtNode(0);
        node.start();

        Scanner key = new Scanner(System.in);
        String cmd = "";
        while(!cmd.equalsIgnoreCase("quit")) {
            cmd = key.nextLine();
            String[] cmdArgs = cmd.split("\\s");
            if(cmdArgs[0].equals("connect")) {
                node.connect(new InetSocketAddress(cmdArgs[1], Integer.parseInt(cmdArgs[2])));
            }
        }
        node.shutdown();
    }
}
