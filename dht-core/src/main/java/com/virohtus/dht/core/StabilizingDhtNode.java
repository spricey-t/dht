package com.virohtus.dht.core;

import com.virohtus.dht.core.engine.DhtManager;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.handler.LoggingHandler;
import com.virohtus.dht.core.network.FingerTable;
import com.virohtus.dht.core.peer.PeerPool;
import com.virohtus.dht.core.peer.PeerPoolHandler;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.transport.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StabilizingDhtNode implements DhtNode {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizingDhtNode.class);

    private final Server server;
    private final HandlerChain handlerChain;
    private final PeerPool peerPool;

    public StabilizingDhtNode() {
        server = null;
        handlerChain = new HandlerChain();
        peerPool = new PeerPool();

        handlerChain.addHandler(new LoggingHandler());
        handlerChain.addHandler(new PeerPoolHandler(peerPool));
        handlerChain.addHandler(new DhtManager(handlerChain));
    }

    @Override
    public void start(int serverPort) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void joinNetwork(ConnectionInfo existingNode) {

    }

    @Override
    public void leaveNetwork() {

    }

    @Override
    public void registerEventHandler(EventHandler handler) {

    }

    @Override
    public void unregisterEventHandler(EventHandler handler) {

    }

    @Override
    public String getNodeId() {
        return null;
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        return null;
    }

    @Override
    public FingerTable getFingerTable() {
        return null;
    }
}
