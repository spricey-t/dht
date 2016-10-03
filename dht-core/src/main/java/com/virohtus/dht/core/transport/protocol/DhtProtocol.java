package com.virohtus.dht.core.transport.protocol;

public interface DhtProtocol {

    int PROTOCOL_VERSION = 1;
    String STRING_ENCODING = "UTF-8";
    int BUFFER_SIZE = 256;
    int HEADER_SIZE = 8;

    int JOIN_NETWORK_REQUEST = 1;
    int JOIN_NETWORK_RESPONSE = 2;
}
