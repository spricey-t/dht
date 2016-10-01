package com.virohtus.dht.core;

import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.Headers;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.DhtServer;
import com.virohtus.dht.core.transport.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class StabilizingDhtNode implements DhtNode, ServerDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);
    private final ExecutorService executorService;
    private final Server server;

    private List<Peer> peers = new ArrayList<>();

    public StabilizingDhtNode(int serverPort) throws IOException {
        executorService = Executors.newCachedThreadPool();
        server = new DhtServer(this, executorService, new InetSocketAddress("localhost", serverPort));
    }

    @Override
    public void connectionOpened(AsynchronousSocketChannel socketChannel) {
        try {
            LOG.info("connection opened: " + socketChannel.getRemoteAddress());
            Peer peer = new Peer(executorService, socketChannel);
            peers.add(peer);
            peer.listen();
        } catch (IOException e) {
        }
    }

    @Override
    public void serverShutdown() {
        LOG.info("server shutdown");
    }

    public void start() throws ExecutionException, InterruptedException {
        Future serverFuture = server.serve();
        LOG.info("server started");
    }


    private void send(String message) throws IOException {
        byte[] data = message.getBytes();
        Headers headers = new Headers(1, data.length);
        DhtEvent event = new DhtEvent(headers, data);
        peers.get(0).send(event);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        StabilizingDhtNode node = new StabilizingDhtNode(0);
        node.start();
    }
}
