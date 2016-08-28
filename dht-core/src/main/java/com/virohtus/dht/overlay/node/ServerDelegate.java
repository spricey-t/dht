package com.virohtus.dht.overlay.node;

import java.net.Socket;

public interface ServerDelegate {
    void onClientConnect(Socket socket);
    void onServerError(Exception e);
}
