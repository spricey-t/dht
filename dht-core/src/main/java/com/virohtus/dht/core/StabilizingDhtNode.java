package com.virohtus.dht.core;

import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.AsyncConnection;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.Headers;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.AsyncServer;
import com.virohtus.dht.core.transport.server.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class StabilizingDhtNode implements DhtNode, ServerDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);
    private static final int SHUTDOWN_TIMEOUT = 3; // seconds
    private final ExecutorService executorService;
    private final Server server;

    private List<Peer> peers = new ArrayList<>();

    public StabilizingDhtNode(int serverPort) throws IOException {
        executorService = Executors.newCachedThreadPool();
        server = new AsyncServer(this, executorService, new InetSocketAddress("localhost", serverPort));
    }

    @Override
    public void connectionOpened(Connection connection) {
        LOG.info("connection opened: ");
        Peer peer = new Peer(executorService, connection);
        peers.add(peer);
        peer.listen();
    }

    @Override
    public void serverShutdown() {
        LOG.info("server shutdown");
    }

    @Override
    public void start() throws ExecutionException, InterruptedException, IOException {
        server.listen();
        SocketAddress serverSocketAddress = server.getSocketAddress();
        LOG.info("server started on port " + ((InetSocketAddress)serverSocketAddress).getPort());
    }

    @Override
    public void shutdown() {
        server.shutdown();
        executorService.shutdown();

        try {
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void send(String message) throws IOException {
        byte[] data = message.getBytes();
        Headers headers = new Headers(1, data.length);
        DhtEvent event = new DhtEvent(headers, data);
        peers.get(0).send(event);
    }

    private void connect(SocketAddress socketAddress) throws IOException, ExecutionException, InterruptedException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        Future connectFuture = socketChannel.connect(socketAddress);
        connectFuture.get();
        Connection connection = new AsyncConnection(executorService, socketChannel);
        Peer peer = new Peer(executorService, connection);
        peers.add(peer);
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
            } else {
                node.send(cmd);
            }
        }
        node.shutdown();
    }

}
