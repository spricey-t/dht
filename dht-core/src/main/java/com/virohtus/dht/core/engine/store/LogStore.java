package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.action.PeerConnected;
import com.virohtus.dht.core.engine.action.ServerStarted;
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

        if(action instanceof PeerConnected) {
            LOG.info("peer connected: " + ((PeerConnected)action).getPeer());
        }
    }
}
