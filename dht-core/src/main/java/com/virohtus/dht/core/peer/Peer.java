package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventFactory;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.ConnectionDelegate;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.util.IdUtil;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Peer implements ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Peer.class);

    private final String peerId;
    private final EventFactory eventFactory = EventFactory.getInstance();
    private final EventHandler eventHandler;
    private final ExecutorService executorService;
    private final PeerType peerType;
    private final Connection connection;
    protected final Resolvable<PeerDetails> peerDetails = new Resolvable<>();

    public Peer(EventHandler handler, ExecutorService executorService, PeerType peerType, Socket socket) throws IOException {
        this.peerId = new IdUtil().generateId();
        this.eventHandler = handler;
        this.executorService = executorService;
        this.peerType = peerType;
        this.connection = new Connection(this, executorService, socket);
    }

    public String getPeerId() {
        return peerId;
    }

    public String getPeerNodeId() throws InterruptedException {
        return peerDetails.get().getNodeId();
    }

    public ConnectionInfo getConnectionInfo() throws InterruptedException {
        return peerDetails.get().getConnectionInfo();
    }

    public void send(Event event) throws IOException {
        connection.send(event.getBytes());
    }

    @Override
    public void dataReceived(byte[] data) {
        try {
            eventHandler.handle(eventFactory.createEvent(data));
        } catch (Exception e) {
            LOG.error("error creating event: " + e.getMessage());
            connection.close();
        }
    }

    @Override
    public void receiveDisrupted(IOException e) {
        eventHandler.handle(new PeerDisconnected(this));
    }
}
