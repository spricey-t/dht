package com.virohtus.dht.event;

public interface EventProtocol {

    String STRING_ENCODING = "UTF-8";

    int HEARTBEAT_EVENT = 1;
    int REQUEST_FINGER_TABLE_EVENT = 2;
    int FINGER_TABLE_RESPONSE_EVENT = 3;

    int ERROR_EVENT = 100;
    int CONNECTION_ERROR = 101;
    int RECEIVER_ERROR = 102;

    int STRING_MESSAGE_EVENT = 200;
}
