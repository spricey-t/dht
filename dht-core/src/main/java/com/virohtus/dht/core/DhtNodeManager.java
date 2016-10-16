package com.virohtus.dht.core;

import com.virohtus.dht.core.network.Network;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.network.NodeManager;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface DhtNodeManager {
    void start() throws ExecutionException, InterruptedException, IOException;
    void shutdown();
    void joinNetwork(SocketAddress socketAddress) throws IOException, InterruptedException, TimeoutException;
    Node getNode();
    Network getNetwork() throws InterruptedException, TimeoutException, PeerNotFoundException, IOException;
}
