package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.transport.protocol.DhtEvent;

import java.io.IOException;

public interface Connection {
    void listen();
    boolean isListening();
    void send(DhtEvent event) throws IOException;
    void setConnectionDelegate(ConnectionDelegate connectionDelegate);
    void close();
}
