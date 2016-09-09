package com.virohtus.dht.server;

import java.net.Socket;

public interface ServerDelegate {
    void onClientConnect(Socket socket);
    void onServerError(Exception e);
}
