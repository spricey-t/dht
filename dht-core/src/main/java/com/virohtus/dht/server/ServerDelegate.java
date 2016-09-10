package com.virohtus.dht.server;

import java.net.Socket;

public interface ServerDelegate {
    void onSocketConnect(Socket socket);
}
