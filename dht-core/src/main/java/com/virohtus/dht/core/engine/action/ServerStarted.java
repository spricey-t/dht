package com.virohtus.dht.core.engine.action;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.transport.server.Server;

public class ServerStarted extends Action {

    private final Server server;

    public ServerStarted(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
