package com.virohtus.dht.overlay.node;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.overlay.transport.*;
import com.virohtus.dht.overlay.transport.tcp.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class OverlayNode implements ServerDelegate, ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(OverlayNode.class);
    private ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
    private ConnectionManager connectionManager;
    private List<Connection> fingers = new ArrayList<>();
    private Server server;

    public OverlayNode(int serverPort) {
        connectionManager = new ConnectionManager();
        server = new TCPServer(this, serverPort);
    }

    public void start() {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
        connectionManager.clear().stream().forEach(connection ->
            connection.close()
        );
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void connect(InetAddress addr, int port) throws IOException {
        Socket socket = new Socket(addr, port);
        Connection connection = connectionFactory.createTCPConnection(this, socket);
        fingers.add(connection);
    }

    public void send(String connectionId, Event event) throws IOException {
        Connection connection = connectionManager.get(connectionId);
        if(connection != null) {
            connection.send(event.getData());
        }
    }

    @Override
    public void onEvent(String connectionId, Event event) {
        switch(event.getType()) {
            case EventProtocol.RECEIVER_ERROR:
            case EventProtocol.CONNECTION_ERROR:
                handleConnectionError(connectionId, (ConnectionError)event);
                break;
        }
    }

    @Override
    public void onClientConnect(Socket socket) {
        try {
            Connection connection = connectionFactory.createTCPConnection(this, socket);
            connectionManager.add(connection);
            LOG.info("client connected: " + connection.getId());
        } catch (IOException e) {
            LOG.error("Failed to create TCPConnection: " + e.getMessage());
        }
    }

    @Override
    public void onServerError(Exception e) {
        LOG.error("encountered server error, shutting down. " + e.getMessage());
        shutdown();
    }

    private void handleConnectionError(String connectionId, ConnectionError event) {
        LOG.info("encountered connection error, closing connection: " + connectionId);
        Connection connection = connectionManager.remove(connectionId);
        connection.close();
    }
}
