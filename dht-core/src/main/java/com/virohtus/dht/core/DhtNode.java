package com.virohtus.dht.core;

import com.virohtus.dht.core.network.NodeIdentity;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface DhtNode {
    void start() throws ExecutionException, InterruptedException, IOException;
    void shutdown();
    void joinNetwork(SocketAddress socketAddress) throws IOException, InterruptedException, TimeoutException;
    NodeIdentity getNodeIdentity();
}
