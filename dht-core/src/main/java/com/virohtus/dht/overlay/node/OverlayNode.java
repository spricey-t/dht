package com.virohtus.dht.overlay.node;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.event.HeartbeatEvent;
import com.virohtus.dht.event.StringMessageEvent;
import com.virohtus.dht.overlay.transport.*;
import com.virohtus.dht.overlay.transport.tcp.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class OverlayNode implements ServerDelegate, ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(OverlayNode.class);
    private final String nodeId;
    private final ConnectionFactory connectionFactory = ConnectionFactory.getInstance();
    private final ConnectionManager connectionManager;
    private final Server server;

    public OverlayNode(int serverPort) {
        nodeId = UUID.randomUUID().toString();
        connectionManager = new ConnectionManager();
        server = new TCPServer(this, serverPort);
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getServerPort() {
        return server.getPort();
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
        Connection connection = connectionFactory.createTCPConnection(ConnectionType.OUTGOING, this, socket);
        connectionManager.add(connection);
    }

    public void send(String connectionId, Event event) throws IOException {
        Connection connection = connectionManager.get(connectionId);
        if(connection != null) {
            connection.send(event.getData());
        }
    }

    public Set<Connection> getOutgoingConnections() {
        return connectionManager
                .list()
                .stream()
                .filter(con -> con.getConnectionType().equals(ConnectionType.OUTGOING))
                .collect(Collectors.toSet());
    }

    public Set<Connection> getIncomingConnections() {
        return connectionManager
                .list()
                .stream()
                .filter(con -> con.getConnectionType().equals(ConnectionType.INCOMING))
                .collect(Collectors.toSet());
    }

    @Override
    public void onEvent(String connectionId, Event event) {
        switch(event.getType()) {
            case EventProtocol.HEARTBEAT_EVENT:
                handleHeartbeatEvent(connectionId, (HeartbeatEvent)event);
                break;
            case EventProtocol.RECEIVER_ERROR:
            case EventProtocol.CONNECTION_ERROR:
                handleConnectionError(connectionId, (ConnectionError)event);
                break;
        }
    }

    @Override
    public void onClientConnect(Socket socket) {
        try {
            Connection connection = connectionFactory.createTCPConnection(ConnectionType.INCOMING, this, socket);
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
        LOG.info("client disconnected: " + connectionId);
        Connection connection = connectionManager.remove(connectionId);
        connection.close();
    }

    private void handleHeartbeatEvent(String connectionId, HeartbeatEvent event) {
        if(event.getStartingOverlayNodeId().equals(this.getNodeId())) {
            // report back to initiator
        }
        // send to successor
    }
}
