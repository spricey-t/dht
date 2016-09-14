package com.virohtus.dht.core;

public interface DhtProtocol {

    String STRING_ENCODING = "UTF-8";

    int PEER_CONNECTED = 1;
    int PEER_DISCONNECTED = 2;
    int PEER_DETAILS_REQUEST = 3;
    int PEER_DETAILS_RESPONSE = 4;
}
