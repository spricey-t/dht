package com.virohtus.dht.event;

public interface EventProtocol {

    String STRING_ENCODING = "UTF-8";

    int CONNECTION_DETAILS_REQUEST = 1;
    int CONNECTION_DETAILS_RESPONSE = 2;
    int GET_OVERLAY = 3;
    int PEER_DETAILS_REQUEST = 4;
    int PEER_DETAILS_RESPONSE = 5;
}
