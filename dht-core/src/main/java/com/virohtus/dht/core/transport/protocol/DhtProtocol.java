package com.virohtus.dht.core.transport.protocol;

public interface DhtProtocol {

    int PROTOCOL_VERSION = 1;
    String STRING_ENCODING = "UTF-8";
    int HEADER_SIZE = 8;
    int REQUEST_TIMEOUT = 10000;


    int JOIN_NETWORK_REQUEST = 1;
    int JOIN_NETWORK_RESPONSE = 2;

    int GET_NODE_IDENTITY_REQUEST = 3;
    int GET_NODE_IDENTITY_RESPONSE = 4;
}
