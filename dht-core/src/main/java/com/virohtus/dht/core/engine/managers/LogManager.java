package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.transport.server.event.ServerShutdown;
import com.virohtus.dht.core.transport.server.event.ServerStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogManager implements Manager {

    private static final Logger LOG = LoggerFactory.getLogger(LogManager.class);

    @Override
    public void handle(String peerId, Event event) {
        switch (event.getType()) {
            case DhtProtocol.SERVER_START:
                handleServerStart((ServerStart)event);
                break;
            case DhtProtocol.SERVER_SHUTDOWN:
                handleServerShutdown((ServerShutdown)event);
                break;
        }
    }

    private void handleServerStart(ServerStart serverStart) {
        LOG.info("server started on port " + serverStart.getPort());
    }

    private void handleServerShutdown(ServerShutdown serverShutdown) {
        LOG.info("server shutdown");
    }

}
