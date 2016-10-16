package com.virohtus.dht.core.transport.protocol;

public interface DhtProtocol {

    String HOSTNAME = "localhost";
    int PROTOCOL_VERSION = 1;
    String STRING_ENCODING = "UTF-8";
    int HEADER_SIZE = 8;
    int REQUEST_TIMEOUT = 10000;
    int NETWORK_TIMEOUT = 30000;
    int GLOBAL_KEYSPACE = 500;
    int STABILIZATION_PERIOD = 2000;


    int JOIN_NETWORK_REQUEST = 1;
    int JOIN_NETWORK_RESPONSE = 2;

    int GET_NODE_IDENTITY_REQUEST = 3;
    int GET_NODE_IDENTITY_RESPONSE = 4;

    int GET_NETWORK = 5;

    int GET_PREDECESSOR_REQUEST = 6;
    int GET_PREDECESSOR_RESPONSE = 7;

    int GET_NODE_REQUEST = 8;
    int GET_NODE_RESPONSE = 9;
}
