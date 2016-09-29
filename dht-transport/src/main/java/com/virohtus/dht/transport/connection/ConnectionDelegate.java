package com.virohtus.dht.transport.connection;

public interface ConnectionDelegate {
    void dataReceived(byte[] data);
    void receiveDisrupted(Exception e);
}
