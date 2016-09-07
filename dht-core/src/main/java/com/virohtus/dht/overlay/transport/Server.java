package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    private Thread serverThread;
    private final int port;
    private final Object startupLock;
    protected final ServerDelegate serverDelegate;

    public Server(ServerDelegate serverDelegate, int port) {
        this.serverDelegate = serverDelegate;
        this.port = port;
        this.startupLock = new Object();
    }

    protected abstract void listen();
    public abstract byte[] getAddress();

    protected void notifyStartupComplete() {
        synchronized (startupLock) {
            startupLock.notify();
        }
    }

    public int getPort() {
        return port;
    }

    public void start() {
        if(serverRunning()) {
            return;
        }

        synchronized (startupLock) {
            serverThread = new Thread(this::listen);
            serverThread.start();
            try {
                startupLock.wait();
            } catch (InterruptedException e) {
                serverDelegate.onServerError(e);
            }
        }
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
