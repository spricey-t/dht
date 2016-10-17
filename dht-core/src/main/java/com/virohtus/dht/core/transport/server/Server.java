package com.virohtus.dht.core.transport.server;

import java.io.IOException;
import java.net.SocketAddress;

public interface Server {
    void listen();
    void shutdown();
    boolean isListening();
    SocketAddress getSocketAddress() throws IOException;
}
