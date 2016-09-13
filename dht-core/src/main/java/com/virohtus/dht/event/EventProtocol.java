package com.virohtus.dht.event;

public interface EventProtocol {

    String STRING_ENCODING = "UTF-8";

    int GET_OVERLAY = 1;
    int PEER_DETAILS_REQUEST = 2;
    int PEER_DETAILS_RESPONSE = 3;
    int FINGER_TABLE_REQUEST = 4;
    int FINGER_TABLE_RESPONSE = 5;
}
