package com.virohtus.dht.core;

public interface DhtProtocol {

    String STRING_ENCODING = "UTF-8";

    int SERVER_START = 1;
    int SERVER_SHUTDOWN = 2;
    int SOCKET_CONNECT = 3;

    int PEER_CONNECTED = 4;
    int PEER_DISCONNECTED = 5;
    int PEER_DETAILS_REQUEST = 6;
    int PEER_DETAILS_RESPONSE = 7;
}
