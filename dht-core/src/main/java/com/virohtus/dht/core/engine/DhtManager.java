package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class DhtManager implements EventHandler {

    private final HandlerChain handlerChain;
    private final ExecutorService executorService;

    public DhtManager(HandlerChain handlerChain, ExecutorService executorService) {
        this.handlerChain = handlerChain;
        this.executorService = executorService;
    }

    @Override
    public void handle(Event event) {
    }

    public void join(ConnectionInfo connectionInfo) throws IOException {
        Peer peer = new Peer(handlerChain, executorService, PeerType.OUTGOING,
                new Socket(connectionInfo.getHost(), connectionInfo.getPort()));
        handlerChain.handle(new PeerConnected(peer));
    }
}
