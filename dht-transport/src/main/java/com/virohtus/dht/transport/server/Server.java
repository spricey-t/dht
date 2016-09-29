package com.virohtus.dht.transport.server;

import com.virohtus.dht.transport.connection.ConnectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private final ServerDelegate serverDelegate;
    private final ExecutorService executorService;
    private final Object serverLock;
    private ServerSocket serverSocket;
    private Future serverFuture;
    private Exception startupException;

    public Server(ServerDelegate serverDelegate, ExecutorService executorService) {
        this.serverDelegate = serverDelegate;
        this.executorService = executorService;
        this.serverLock = new Object();
    }

    public void start(int port) throws IOException {
    }

    public void close() throws IOException {
        serverSocket.close();
    }

    public boolean isAlive() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    public ConnectionInfo getConnectionInfo() {
        return null;
    }

    private void listen(int port) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                serverDelegate.socketConnected(serverSocket.accept());
            }
        } catch(IOException e) {
            // serverSocket must have been closed
            serverDelegate.serverDisrupted(e);
        }
    }
}
