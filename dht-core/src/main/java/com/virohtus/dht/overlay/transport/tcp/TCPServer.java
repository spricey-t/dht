package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.overlay.node.ServerDelegate;
import com.virohtus.dht.overlay.transport.Server;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPServer extends Server {

    private ServerSocket serverSocket;

    public TCPServer(ServerDelegate serverDelegate, int port) {
        super(serverDelegate, port);
    }

    @Override
    protected void listen() {
        try {
            serverSocket = new ServerSocket(getPort());
            while(!Thread.currentThread().isInterrupted()) {
                serverDelegate.onClientConnect(serverSocket.accept());
            }
        } catch (IOException e) {
            serverDelegate.onServerError(e);
        }
    }
}
