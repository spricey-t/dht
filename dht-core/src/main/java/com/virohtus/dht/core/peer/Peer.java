package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.ConnectionDelegate;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class Peer implements ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Peer.class);
    private final ExecutorService executorService;
    private final Connection connection;

    public Peer(ExecutorService executorService, Connection connection) {
        this.executorService = executorService;
        this.connection = connection;
        connection.setConnectionDelegate(this);
    }

    public void listen() {
        connection.listen();
    }

    public void send(DhtEvent data) throws IOException {
        connection.send(data);
    }

    @Override
    public void dataReceived(DhtEvent data) {
        LOG.info("received dht packet: " + new String(data.getPayload()));
    }
}
