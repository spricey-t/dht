package com.virohtus.dht.transport.connection;

/**
 * Represents an internet connection to another node.
 * Purpose is to send and receive data, ensuring consistency
 */
public interface Connection {
    void send(byte[] data);
    void receive();
    void close();
}
