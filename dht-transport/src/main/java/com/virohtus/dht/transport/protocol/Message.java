package com.virohtus.dht.transport.protocol;

public class Message {

    public static final int VERSION = 1;
    private final byte[] headers;
    private final byte[] payload;

    public Message(byte[] headers, byte[] payload) {
        this.headers = headers;
        this.payload = payload;
    }

    public byte[] getHeaders() {
        return headers;
    }

    public byte[] getPayload() {
        return payload;
    }
}
