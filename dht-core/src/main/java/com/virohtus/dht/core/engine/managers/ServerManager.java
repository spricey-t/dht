package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.TCPServer;
import com.virohtus.dht.core.transport.server.event.ServerShutdown;
import com.virohtus.dht.core.transport.server.event.ServerStart;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class ServerManager implements Manager {

    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final Server server;

    public ServerManager(Dispatcher dispatcher, ExecutorService executorService) {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        this.server = new TCPServer(dispatcher, executorService);
    }

    @Override
    public void handle(String peerId, Event event) {
    }

    public void start(int port) throws IOException {
        server.start(port);
        dispatcher.dispatch(null, new ServerStart(server.getConnectionInfo().getPort()));
    }

    public void shutdown() {
        server.shutdown();
        dispatcher.dispatch(null, new ServerShutdown());
    }

    public boolean isAlive() {
        return server.isAlive();
    }

    public ConnectionInfo getConnectionInfo() {
        return server.getConnectionInfo();
    }
}
