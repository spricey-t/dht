package com.virohtus.dht.core.transport.server.handler;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.handler.HandlerChain;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.peer.event.PeerConnected;
import com.virohtus.dht.core.transport.server.event.SocketConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class SocketConnectionHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SocketConnectionHandler.class);
    private final HandlerChain handlerChain;
    private final ExecutorService executorService;

    public SocketConnectionHandler(HandlerChain handlerChain, ExecutorService executorService) {
        this.handlerChain = handlerChain;
        this.executorService = executorService;
    }

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.SOCKET_CONNECT:
                handleSocketConnectEvent((SocketConnect)event);
                break;
        }
    }

    private void handleSocketConnectEvent(SocketConnect event) {
        try {
            Peer peer = new Peer(handlerChain, executorService, PeerType.INCOMING, event.getSocket());
            handlerChain.handle(peer.getPeerId(), new PeerConnected(peer));
        } catch (IOException e) {
            LOG.error("failed to create peer from socket: " + e.getMessage());
            try {
                event.getSocket().close();
            } catch (IOException e1) {
                LOG.error("failed to close broken socket: " + e1.getMessage());
            }
        }
    }

}
