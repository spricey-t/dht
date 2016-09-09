package com.virohtus.dht.node;

import com.virohtus.dht.server.Server;
import com.virohtus.dht.server.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Node implements ServerDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final ExecutorService executorService;
    private Server server;

    public Node() {
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void onClientConnect(Socket socket) {
        LOG.info("client connected");
    }

    public void run() throws IOException {
        if(isServerAlive()) {
            return;
        }
        server = new Server(this, executorService, 0);
        server.start();
        server.join();
    }

    public int getServerPort() {
        if(isServerAlive()) {
            return -1;
        }
        return server.getPort();
    }

    private boolean isServerAlive() {
        return server != null && server.isAlive();
    }
}
