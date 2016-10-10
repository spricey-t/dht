package com.virohtus.dht.core;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;

public interface DhtNode {
    void start() throws ExecutionException, InterruptedException, IOException;
    void shutdown();
    void joinNetwork(SocketAddress socketAddress);
    NodeIdentity getNodeIdentity();
}
