package com.virohtus.dht.transport.server;

public interface Server {
    void listen();
    void shutdown();
    boolean isListening();
}
