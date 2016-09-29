package com.virohtus.dht.transport.server;

import java.net.Socket;

public interface ServerDelegate {
    void socketConnected(Socket socket);
    void serverDisrupted(Exception e);
}
