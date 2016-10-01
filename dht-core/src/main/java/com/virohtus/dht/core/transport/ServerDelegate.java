package com.virohtus.dht.core.transport;

import java.nio.channels.AsynchronousSocketChannel;

public interface ServerDelegate {
    void connectionOpened(AsynchronousSocketChannel socketChannel);
    void serverShutdown();
}
