package com.virohtus.dht.core;

public interface DhtProtocol {

    String STRING_ENCODING = "UTF-8";

    int SERVER_START = 1;
    int SERVER_SHUTDOWN = 2;
    int SOCKET_CONNECT = 3;

    int PEER_CONNECTED = 4;
    int PEER_DISCONNECTED = 5;

    int NODE_IDENTITY_REQUEST = 6;
    int NODE_IDENTITY_RESPONSE = 7;

    int GET_DHT_NETWORK = 8;
    int SET_PREDECESSOR_REQUEST = 9;
}
