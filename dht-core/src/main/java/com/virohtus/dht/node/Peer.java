package com.virohtus.dht.node;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.utils.DhtUtilities;

import java.net.Socket;

public class Peer {

    private final String id;
    private final Socket socket;
    private ConnectionDetails connectionDetails;

    public Peer(Socket socket) {
        this.id = DhtUtilities.getInstance().generateId();
        this.socket = socket;
        this.connectionDetails = requestConnectionDetails();
    }

    public void send() {
    }

    private ConnectionDetails requestConnectionDetails() {
        return null;
    }
}
