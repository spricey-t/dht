package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.engine.action.peer.PeerConnected;
import com.virohtus.dht.core.engine.action.peer.PeerDisconnected;
import com.virohtus.dht.core.engine.action.server.ServerShutdown;
import com.virohtus.dht.core.engine.action.server.ServerStarted;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class LogStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(LogStore.class);

    @Override
    public void onAction(Action action) {

        if(action instanceof ServerStarted) {
            try {
                LOG.info("server started on port: " + ((InetSocketAddress)((ServerStarted)action).getServer().getSocketAddress()).getPort());
            } catch (IOException e) {
                LOG.warn("could not get socket address for server! " + e);
            }
        }

        if(action instanceof ServerShutdown) {
            LOG.info("server shutdown");
        }

        if(action instanceof PeerConnected) {
            LOG.info("peer connected: " + ((PeerConnected)action).getPeer());
        }

        if(action instanceof PeerDisconnected) {
            LOG.info("peer disconnected " + ((PeerDisconnected)action).getPeer());
        }

        if(action instanceof TransportableAction) {
            TransportableAction transportableAction = (TransportableAction)action;
            switch (transportableAction.getType()) {
                case DhtProtocol.GET_NODE_IDENTITY_REQUEST:
                    LOG.info("received node identity request");
                    break;
                case DhtProtocol.GET_NODE_IDENTITY_RESPONSE:
                    LOG.info("received node identity response");
                    break;
            }
        }
    }
}
