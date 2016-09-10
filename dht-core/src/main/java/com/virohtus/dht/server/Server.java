package com.virohtus.dht.server;

import com.virohtus.dht.connection.ConnectionDetails;
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
    private Exception startupException;

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

    public ConnectionDetails getConnectionDetails() {
        return new ConnectionDetails(serverSocket.getInetAddress().getAddress(), serverSocket.getLocalPort());
    }

    public void start() throws IOException {
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
            if(startupException != null) {
                throw new IOException(startupException);
            }
        }
    }

    public void shutdown() {
        if(!isAlive()) {
            return;
        }
        synchronized (serverLock) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOG.error("failed to fully flush data on server shutdown: " + e.getMessage());
            }
            try {
                serverLock.wait();
            } catch (InterruptedException e) {
                // someone didn't want to wait for wait()
                LOG.warn("server shutdown force stopped when waiting for graceful shutdown");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void join() {
        if(!isAlive()) {
            return;
        }
        synchronized (serverLock) {
            try {
                serverLock.wait();
            } catch (InterruptedException e) {
                // someone didn't want to wait for wait()
                LOG.warn("server join force stopped when waiting for graceful join");
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
        Thread.currentThread().setName(Thread.currentThread().getName() + "-" + this.getClass().getSimpleName());
        try {
            synchronized (serverLock) {
                serverSocket = new ServerSocket(requestedPort);
                serverLock.notifyAll();
            }
            LOG.info("server started on port: " + serverSocket.getLocalPort());
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    serverDelegate.onSocketConnect(socket);
                }
            } catch(IOException e) {
                // serverSocket must have been closed
            }
            synchronized (serverLock) {
                serverLock.notify();
            }
        } catch (IOException e) {
            startupException = e;
            synchronized (serverLock) {
                serverLock.notifyAll();
            }
        }
        LOG.info("server closed");
    }
}
