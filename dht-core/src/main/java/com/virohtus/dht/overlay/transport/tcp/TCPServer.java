package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.overlay.node.ServerDelegate;
import com.virohtus.dht.overlay.transport.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServer extends Server {

    private static final Logger LOG = LoggerFactory.getLogger(TCPServer.class);
    private ServerSocket serverSocket;

    public TCPServer(ServerDelegate serverDelegate, int port) {
        super(serverDelegate, port);
    }

    @Override
    public int getPort() {
        return serverSocket == null ? 0 : serverSocket.getLocalPort();
    }

    @Override
    protected void listen() {
        try {
            serverSocket = new ServerSocket(getPort());
            while(!Thread.currentThread().isInterrupted()) {
                serverDelegate.onClientConnect(serverSocket.accept());
            }
        } catch (IOException e) {
            // if the server socket was closed intentionally, do not propagate error
            if(!Thread.currentThread().isInterrupted()) {
                serverDelegate.onServerError(e);
            }
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOG.error("failed to close server socket: " + e.getMessage());
        }
    }
}
