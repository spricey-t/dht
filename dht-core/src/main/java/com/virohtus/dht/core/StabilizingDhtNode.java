package com.virohtus.dht.core;

import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.store.LogStore;
import com.virohtus.dht.core.engine.store.ServerStore;
import com.virohtus.dht.core.engine.SingleThreadedDispatcher;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.engine.store.PeerStore;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.AsyncConnection;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;
import java.util.concurrent.*;

public class StabilizingDhtNode implements DhtNode {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);
    private static final int SHUTDOWN_TIMEOUT = 3; // seconds
    private final ExecutorService executorService;
    private final Dispatcher dispatcher;
    private final ServerStore serverStore;
    private final PeerStore peerStore;

    public StabilizingDhtNode(int serverPort) throws IOException {
        executorService = Executors.newCachedThreadPool();
        dispatcher = new SingleThreadedDispatcher(executorService);

        serverStore = new ServerStore(dispatcher, executorService, new InetSocketAddress("localhost", serverPort));
        peerStore = new PeerStore(dispatcher, executorService);

        dispatcher.registerStore(new LogStore());
        dispatcher.registerStore(serverStore);
        dispatcher.registerStore(peerStore);
    }

    @Override
    public void start() throws ExecutionException, InterruptedException, IOException {
        serverStore.start();
        dispatcher.start();
    }

    @Override
    public void shutdown() {
        serverStore.shutdown();
        dispatcher.shutdown();
        executorService.shutdown();

        try {
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
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
