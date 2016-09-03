package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    private Thread serverThread;
    private final int port;
    protected final ServerDelegate serverDelegate;

    public Server(ServerDelegate serverDelegate, int port) {
        this.serverDelegate = serverDelegate;
        this.port = port;
    }

    protected abstract void listen();

    public int getPort() {
        return port;
    }

    public void start() {
        if(serverRunning()) {
            return;
        }

        serverThread = new Thread(this::listen);
        serverThread.start();
        LOG.info("server started");
    }

    public void shutdown() {
        if(!serverRunning()) {
            return;
        }
        serverThread.interrupt();
        LOG.info("shutting down server");
    }

    public void join() throws InterruptedException {
        if(serverThread != null) {
            serverThread.join();
        }
    }

    public boolean serverRunning() {
        return serverThread != null && serverThread.isAlive();
    }
}
