package com.virohtus.dht.core.handler;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHandler.class);

    @Override
    public void handle(Event event) {
        switch (event.getType()) {
            case DhtProtocol.PEER_DISCONNECTED:
                LOG.info("peer disconnected: " + ((PeerDisconnected)event).toString());
                break;
            case DhtProtocol.PEER_DETAILS_REQUEST:
                break;
            case DhtProtocol.PEER_DETAILS_RESPONSE:
                break;
        }
    }

}
