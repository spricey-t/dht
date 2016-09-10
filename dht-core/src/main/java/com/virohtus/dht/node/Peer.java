package com.virohtus.dht.node;

import com.virohtus.dht.connection.Connection;
import com.virohtus.dht.connection.ConnectionDelegate;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventFactory;
import com.virohtus.dht.utils.DhtUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Peer implements ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Peer.class);
    private final String id;
    private final PeerDelegate peerDelegate;
    private final ExecutorService executorService;
    private final Connection connection;
    private final PeerType peerType;
    private final EventFactory eventFactory;

    public Peer(PeerDelegate peerDelegate, ExecutorService executorService, PeerType peerType, Socket socket) throws IOException {
        this.id = DhtUtilities.getInstance().generateId();
        this.peerDelegate = peerDelegate;
        this.executorService = executorService;
        this.connection = new Connection(this, executorService, socket);
        this.peerType = peerType;
        this.eventFactory = EventFactory.getInstance();
    }

    @Override
    public void dataReceived(byte[] data) {
        Event event;
        try {
            event = eventFactory.createEvent(data);
        } catch (Exception e) {
            LOG.error("failed to construct event! " + e.getMessage());
            return;
        }
        peerDelegate.peerEventReceived(this, event);
    }

    @Override
    public void receiveDisrupted(IOException e) {
        peerDelegate.peerDisconnected(this);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", id, connection.toString());
    }

    public String getId() {
        return id;
    }

    public PeerType getPeerType() {
        return peerType;
    }

    public void send(Event event) throws IOException {
        LOG.info("sending event: " + event.getClass().getSimpleName());
        connection.send(event.getBytes());
    }

    public void close() {
        connection.close();
    }
}
