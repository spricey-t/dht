package com.virohtus.dht.core.engine.action.server;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.transport.server.Server;

public class ServerShutdown extends Action {

    private Server server;

    public ServerShutdown(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
