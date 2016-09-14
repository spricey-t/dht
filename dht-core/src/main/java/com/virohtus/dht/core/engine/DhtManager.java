package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;

public class DhtManager implements EventHandler {

    private final HandlerChain handlerChain;

    public DhtManager(HandlerChain handlerChain) {
        this.handlerChain = handlerChain;
    }

    @Override
    public void handle(Event event) {
    }

    public void join(ConnectionInfo connectionInfo) {
        Peer peer = new Peer(connectionInfo);
    }
}
