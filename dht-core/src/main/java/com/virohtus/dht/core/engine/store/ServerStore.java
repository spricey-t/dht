package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.ServerStarted;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.server.AsyncServer;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.ServerDelegate;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;

public class ServerStore implements Store, ServerDelegate {


    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final Server server;

    public ServerStore(Dispatcher dispatcher, ExecutorService executorService,
                       SocketAddress socketAddress) throws IOException {
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        server = new AsyncServer(this, executorService, socketAddress);
    }

    public void start() {
        server.listen();
        dispatcher.dispatch(new ServerStarted(server));
    }

    public void shutdown() {
        server.shutdown();
        //todo
        //dispatcher.dispatch();
    }

    public boolean isAlive() {
        return server.isListening();
    }

    @Override
    public void onAction(Action action) {
    }

    @Override
    public void connectionOpened(Connection connection) {

    }

    @Override
    public void serverShutdown() {

    }
}
