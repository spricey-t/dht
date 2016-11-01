package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.protocol.Message;

public interface Connection {
    void send(Message message) throws ConnectionException;
    void listen();
    void shutdown();
    boolean isListening();
}
