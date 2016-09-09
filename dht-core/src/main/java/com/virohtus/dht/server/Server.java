package com.virohtus.dht.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);
    private final ServerDelegate serverDelegate;
    private final int requestedPort;
    private final ExecutorService executorService;
    private final Object serverLock;
    private ServerSocket serverSocket;
    private Future serverFuture;

    public Server(ServerDelegate serverDelegate, ExecutorService executorService, int serverPort) {
        this.serverDelegate = serverDelegate;
        this.executorService = executorService;
        this.requestedPort = serverPort;
        this.serverLock = new Object();
    }

    public int getRequestedPort() {
        return requestedPort;
    }

    public int getPort() {
        if(!isAlive()) {
            return -1;
        }
        return serverSocket.getLocalPort();
    }

    public void start() {
        if(isAlive()) {
            return;
        }
        synchronized (serverLock) {
            this.serverFuture = executorService.submit(this::listen);
            try {
                serverLock.wait();
            } catch (InterruptedException e) {
                // someone didn't want to wait for wait()
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() throws IOException {
        if(!isAlive()) {
            return;
        }
        synchronized (serverLock) {
            serverSocket.close();
            try {
                serverLock.wait();
            } catch (InterruptedException e) {
                // someone didn't want to wait for wait()
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isAlive() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    private void listen() {
        if(isAlive()) {
            return;
        }
        try {
            synchronized (serverLock) {
                serverSocket = new ServerSocket(requestedPort);
                serverLock.notify();
            }
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    serverDelegate.onClientConnect(socket);
                }
            } catch(IOException e) {
                // serverSocket must have been closed
                LOG.info("server closed");
            }
            synchronized (serverLock) {
                serverLock.notify();
            }
        } catch (IOException e) {
            serverDelegate.onServerError(e);
        }
    }
}
