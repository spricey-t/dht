package com.virohtus.dht.core.transport.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.Future;

public interface Server {
    Future serve();
    SocketAddress getSocketAddress() throws IOException;
}
