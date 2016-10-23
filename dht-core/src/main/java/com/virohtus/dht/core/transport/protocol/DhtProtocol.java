package com.virohtus.dht.core.transport.protocol;

public interface DhtProtocol {

    String HOSTNAME = "localhost";
    int PROTOCOL_VERSION = 1;
    String STRING_ENCODING = "UTF-8";
    int HEADER_SIZE = 8;
    int REQUEST_TIMEOUT = 10000;
    int FORWARDED_REQUEST_TIMEOUT = 30000;
    int NETWORK_TIMEOUT = 30000;
    int GLOBAL_KEYSPACE = 500;
    int STABILIZATION_PERIOD = 5000;


    int JOIN_NETWORK_REQUEST = 1;
    int JOIN_NETWORK_RESPONSE = 2;

    int GET_NODE_IDENTITY_REQUEST = 3;
    int GET_NODE_IDENTITY_RESPONSE = 4;

    int GET_NETWORK = 5;

    int GET_NODE_REQUEST = 8;
    int GET_NODE_RESPONSE = 9;

    int SET_PREDECESSOR = 10;

    int UPDATE_KEYSPACE = 11;

    int GET_SUCCESSOR_REQUEST = 12;
    int GET_SUCCESSOR_RESPONSE = 13;
}
