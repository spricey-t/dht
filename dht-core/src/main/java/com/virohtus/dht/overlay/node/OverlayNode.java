package com.virohtus.dht.overlay.node;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.overlay.transport.ConnectionFactory;
import com.virohtus.dht.overlay.transport.ConnectionManager;
import com.virohtus.dht.overlay.transport.Server;
import com.virohtus.dht.overlay.transport.tcp.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public abstract class OverlayNode implements ServerDelegate, ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(OverlayNode.class);
    private ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
    private ConnectionManager connectionManager;
    private Server server;

    public OverlayNode(int serverPort) {
        server = new TCPServer(this, serverPort);
    }

    public void start() {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    @Override
    public void onClientConnect(Socket socket) {
        try {
            connectionManager.add(connectionFactory.createTCPConnection(this, socket));
        } catch (IOException e) {
            LOG.error("Failed to create TCPConnection: " + e.getMessage());
        }
    }

    @Override
    public void onServerError(Exception e) {
        LOG.error("encountered server error, shutting down. " + e.getMessage());
        shutdown();
    }
}
