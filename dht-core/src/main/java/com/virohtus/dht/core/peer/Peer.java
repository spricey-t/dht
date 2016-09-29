package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventFactory;
import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.event.NodeIdentityRequest;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.ConnectionDelegate;
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
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final PeerType peerType;
    private final Connection connection;
    public final Resolvable<NodeIdentity> nodeIdentity = new Resolvable<>(DhtProtocol.NODE_TIMEOUT);

    public Peer(Dispatcher dispatcher, ExecutorService executorService, PeerType peerType, Socket socket) throws IOException {
        this.peerId = new IdUtil().generateId();
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        this.peerType = peerType;
        this.connection = new Connection(this, executorService, socket);
    }

    public String getPeerId() {
        return peerId;
    }

    public PeerType getPeerType() {
        return peerType;
    }

    public NodeIdentity getNodeIdentity() throws InterruptedException {
        return nodeIdentity.get();
    }

    public void send(Event event) throws IOException {
        connection.send(event.getBytes());
    }

    public void listen() {
        connection.listen();
    }

    public void shutdown() {
        connection.close();
    }

    @Override
    public void dataReceived(byte[] data) {
        try {
            dispatcher.dispatch(getPeerId(), eventFactory.createEvent(data));
        } catch (Exception e) {
            LOG.error("error creating event: " + e.getMessage() + " for peer: " + getPeerId());
            connection.close();
        }
    }

    @Override
    public void receiveDisrupted(IOException e) {
        dispatcher.dispatch(getPeerId(), new PeerDisconnected(this));
    }

    @Override
    public String toString() {
        return String.format("peerId: %s type: %s", getPeerId(), getPeerType());
    }
}
