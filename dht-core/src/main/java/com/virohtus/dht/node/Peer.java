package com.virohtus.dht.node;

import com.virohtus.dht.connection.Connection;
import com.virohtus.dht.connection.ConnectionDelegate;
import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.connection.event.ConnectionDetailsRequest;
import com.virohtus.dht.connection.event.ConnectionDetailsResponse;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventFactory;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.event.UnsupportedEventException;
import com.virohtus.dht.utils.DhtUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Peer implements ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Peer.class);
    private final String id;
    private final PeerDelegate peerDelegate;
    private final ExecutorService executorService;
    private final Connection connection;
    private final PeerType peerType;
    private final EventFactory eventFactory;
    private ConnectionDetails connectionDetails;
    private final Object connectionDetailsLock = new Object();

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

        LOG.info("received event: " + event.getClass().getSimpleName());
        switch (event.getType()) {
            // peer doesn't know about the node -- therefore it doesn't know
            // the server details to respond to a CONNECTION_DETAILS_REQUEST
            case EventProtocol.CONNECTION_DETAILS_RESPONSE:
                onConnectionDetailsReceived((ConnectionDetailsResponse) event);
                break;
        }
        peerDelegate.peerEventReceived(this, event);
    }

    @Override
    public void receiveDisrupted(IOException e) {
        if(!(e instanceof EOFException)) {
            LOG.error("receive error! " + e.getMessage());
        }
        peerDelegate.peerDisconnected(this);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", id, connection.toString());
    }

    public String getId() {
        return id;
    }

    public ConnectionDetails getConnectionDetails() throws IOException, InterruptedException {
        if(connectionDetails == null) {
            synchronized (connectionDetailsLock) {
                send(new ConnectionDetailsRequest()); // response will set the member variable and notify
                connectionDetailsLock.wait();
            }
        }
        return connectionDetails;
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

    private void onConnectionDetailsReceived(ConnectionDetailsResponse response) {
        synchronized (connectionDetailsLock) {
            connectionDetails = response.getConnectionDetails();
            connectionDetailsLock.notifyAll();
        }
    }
}
