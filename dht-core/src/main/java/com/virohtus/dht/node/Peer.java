package com.virohtus.dht.node;

import com.virohtus.dht.connection.Connection;
import com.virohtus.dht.connection.ConnectionDelegate;
import com.virohtus.dht.connection.ConnectionDetails;
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
    private final PeerManager peerManager;
    private final Connection connection;
    private ConnectionDetails connectionDetails;

    public Peer(PeerManager peerManager, ExecutorService executorService, Socket socket) throws IOException {
        this.id = DhtUtilities.getInstance().generateId();
        this.peerManager = peerManager;
        this.connection = new Connection(this, executorService, socket);
        this.connectionDetails = requestConnectionDetails();
    }

    @Override
    public void dataReceived(byte[] data) {

    }

    @Override
    public void receiveDisrupted(IOException e) {
        if(!(e instanceof EOFException)) {
            LOG.error("receive error! " + e.getMessage());
        }
        peerManager.onPeerDisconnect(this);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", id, connection.toString());
    }

    public void send() {
    }

    public String getId() {
        return id;
    }

    private ConnectionDetails requestConnectionDetails() {
        return null;
    }
}
