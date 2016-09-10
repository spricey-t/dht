package com.virohtus.dht.node;

import com.virohtus.dht.server.Server;
import com.virohtus.dht.server.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node implements ServerDelegate, PeerDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final ExecutorService executorService;
    private final PeerManager peerManager;
    private Server server;

    public Node() {
        this.executorService = Executors.newCachedThreadPool((runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName(this.getClass().getSimpleName());
            return thread;
        });
        this.peerManager = new PeerManager(this);
    }

    @Override
    public void onSocketConnect(Socket socket) {
        peerManager.createPeer(socket);
    }

    public void start() throws IOException {
        if(isServerAlive()) {
            return;
        }
        server = new Server(this, executorService, 0);
        server.start();
        server.join();
    }

    public void waitForCompletion() {
        if(!isServerAlive()) {
            return;
        }
        server.join();
    }

    public void shutdown() {
        if(!isServerAlive()) {
            return;
        }
        server.shutdown();
        waitForCompletion();
    }

    public int getServerPort() {
        if(!isServerAlive()) {
            return -2;
        }
        return server.getPort();
    }

    private boolean isServerAlive() {
        return server != null && server.isAlive();
    }
}
