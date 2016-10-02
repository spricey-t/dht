package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.action.ActionFactory;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.ConnectionDelegate;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.util.IdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class Peer implements ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Peer.class);
    private final String id;
    private final ExecutorService executorService;
    private final Connection connection;
    private final ActionFactory actionFactory = ActionFactory.getInstance();

    public Peer(ExecutorService executorService, Connection connection) {
        id = new IdService().generateId();
        this.executorService = executorService;
        this.connection = connection;
        connection.setConnectionDelegate(this);
    }

    public String getId() {
        return id;
    }

    public void listen() {
        connection.listen();
    }

    public void send(DhtEvent data) throws IOException {
        connection.send(data);
    }

    public void shutdown() {
        connection.close();
        // todo dispatch
    }

    @Override
    public void dataReceived(DhtEvent event) {
        try {
            Action action = actionFactory.createAction(event);
            // todo send to dispatcher
        } catch (IOException e) {
            LOG.warn("receive failure: " + e.getMessage());
            shutdown();
        }
        LOG.info("received dht packet: " + new String(event.getPayload()));
    }

    @Override
    public void listenerDisrupted() {
        shutdown();
    }
}
