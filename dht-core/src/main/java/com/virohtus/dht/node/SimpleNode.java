package com.virohtus.dht.node;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventHandler;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.event.PingEvent;
import com.virohtus.dht.transport.connection.Connection;
import com.virohtus.dht.transport.connection.ConnectionInfo;
import com.virohtus.dht.transport.connection.SendFailedException;
import com.virohtus.dht.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleNode implements Node, EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleNode.class);
    private static final int SHUTDOWN_TIMEOUT = 20;
    private final Set<EventHandler> eventHandlers = new HashSet<>();
    private final Map<Integer, Connection> connections = new HashMap<>();
    private final int requestedServerPort;
    private final ExecutorService executorService;
    private Server server;

    public SimpleNode(int serverPort, int numThreads) {
        this.requestedServerPort = serverPort;
        this.executorService = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public void registerEventHandler(EventHandler eventHandler) {
        synchronized (eventHandlers) {
            eventHandlers.add(eventHandler);
        }
    }

    @Override
    public void send(int connectionId, Event event) throws SendFailedException {
        Connection connection;
        synchronized (connections) {
            connection = connections.get(connectionId);
        }
        if(connection == null) {
            // todo throw connection not found exception
        }
        try {
            connection.send(event.getData());
        } catch (IOException e) {
            throw new SendFailedException("could not serialize event ", e);
        }
    }

    @Override
    public void connect(ConnectionInfo connectionInfo) {
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return server.getConnectionInfo();
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {
    }

    @Override
    public void waitForCompletion() {
        try {
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    @Override
    public void onEvent(int connectionId, Event event) {
        switch (event.getType()) {
            case EventProtocol.PING_EVENT:
                handlePingEvent(connectionId, (PingEvent)event);
                break;
        }

        synchronized (eventHandlers) {
            eventHandlers.stream().forEach(handler -> handler.onEvent(connectionId, event));
        }
    }

    private void handlePingEvent(int connectionId, PingEvent pingEvent) {
        try {
            if(pingEvent.getOriginator() == getConnectionInfo().hashCode()) {
                Connection connection;
                synchronized (connections) {
                    connection = connections.get(connectionId);
                }
                LOG.info("ping completed to " + connection.getConnectionInfo());
            } else {
                send(connectionId, pingEvent);
            }
        } catch (SendFailedException e) {
            LOG.error("Failed to respond to ping event: " + e.getMessage());
        }
    }


    public static void main(String[] args) throws SendFailedException, InterruptedException {
        Node node1 = new SimpleNode(0, 36);
        Node node2 = new SimpleNode(0, 36);

        node1.start();
        node2.start();

        node1.connect(node2.getConnectionInfo());
        int node2ConId = node2.getConnectionInfo().hashCode();
        node1.send(node2ConId, new PingEvent(node2ConId));

        Thread.sleep(2000);
        node1.shutdown();
        node2.shutdown();
        node1.waitForCompletion();
        node2.waitForCompletion();
    }
}
