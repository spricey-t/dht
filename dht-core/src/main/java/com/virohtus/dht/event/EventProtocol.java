package com.virohtus.dht.event;

public interface EventProtocol {

    String STRING_ENCODING = "UTF-8";

    int GET_OVERLAY = 1;
    int PEER_DETAILS_REQUEST = 2;
    int PEER_DETAILS_RESPONSE = 3;

    int FINGER_TABLE_REQUEST = 4; //still need these?
    int FINGER_TABLE_RESPONSE = 5;

    int SET_PREDECESSOR_REQUEST = 6;
    int GET_PREDECESSOR_REQUEST = 7;
    int GET_PREDECESSOR_RESPONSE = 8;
}
