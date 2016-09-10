package com.virohtus.dht.node;

import com.virohtus.dht.connection.Connection;
import com.virohtus.dht.connection.ConnectionDelegate;
import com.virohtus.dht.connection.ConnectionDetails;
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
    private final Connection connection;
    private final EventFactory eventFactory;
    private ConnectionDetails connectionDetails;

    public Peer(PeerDelegate peerDelegate, ExecutorService executorService, Socket socket) throws IOException {
        this.id = DhtUtilities.getInstance().generateId();
        this.peerDelegate = peerDelegate;
        this.connection = new Connection(this, executorService, socket);
        this.eventFactory = EventFactory.getInstance();
        this.connectionDetails = requestConnectionDetails();
    }

    @Override
    public void dataReceived(byte[] data) {
        peerDelegate.peerEventReceived(this, eventFactory.createEvent(data));
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

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public void send(Event event) {
        throw new RuntimeException("not implemented");
    }

    public String getId() {
        return id;
    }

    private ConnectionDetails requestConnectionDetails() {
        throw new RuntimeException("not implemented");
    }
}
