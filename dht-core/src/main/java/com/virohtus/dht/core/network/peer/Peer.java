package com.virohtus.dht.core.network.peer;

import com.virohtus.dht.core.action.*;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityRequest;
import com.virohtus.dht.core.engine.action.network.GetNodeIdentityResponse;
import com.virohtus.dht.core.engine.action.peer.PeerDisconnected;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.connection.ConnectionDelegate;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.util.IdService;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class Peer implements ConnectionDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Peer.class);
    private final String id;
    private PeerType type;
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private final Connection connection;
    private final ActionFactory actionFactory = ActionFactory.getInstance();
    private final Map<String, Resolvable<ResponseAction>> pendingRequests;
    private final Resolvable<NodeIdentity> nodeIdentityResolvable;

    public Peer(Dispatcher dispatcher, ExecutorService executorService, PeerType type, Connection connection) {
        id = new IdService().generateId();
        this.dispatcher = dispatcher;
        this.executorService = executorService;
        this.type = type;
        this.connection = connection;
        this.pendingRequests = new HashMap<>();
        this.nodeIdentityResolvable = new Resolvable<>(DhtProtocol.REQUEST_TIMEOUT);
        connection.setConnectionDelegate(this);
    }

    public String getId() {
        return id;
    }

    public PeerType getType() {
        return type;
    }

    public void setType(PeerType peerType) {
        this.type = peerType;
    }

    public Connection getConnection() {
        return connection;
    }

    public NodeIdentity getNodeIdentity() throws IOException, TimeoutException, InterruptedException {
        if(!nodeIdentityResolvable.valuePresent()) {
            GetNodeIdentityResponse response = sendRequest(new GetNodeIdentityRequest(), GetNodeIdentityResponse.class).get();
            nodeIdentityResolvable.resolve(response.getNodeIdentity());
        }
        return nodeIdentityResolvable.get();
    }

    public boolean hasNodeIdentity() {
        return nodeIdentityResolvable.valuePresent();
    }

    public void listen() {
        connection.listen();
    }

    public void send(byte[] data) throws IOException {
        send(new DhtEvent(data));
    }

    public void send(DhtEvent data) throws IOException {
        connection.send(data);
    }

    public <T extends ResponseAction> Resolvable<T> sendRequest(RequestAction requestAction, Class<T> responseClass) throws IOException {
        synchronized (pendingRequests) {
            send(requestAction.serialize());
            pendingRequests.put(requestAction.getRequestId(), new Resolvable<>(DhtProtocol.REQUEST_TIMEOUT));
        }
        Resolvable<ResponseAction> responseResolvable;
        synchronized (pendingRequests) {
            responseResolvable = pendingRequests.get(requestAction.getRequestId());
        }
        return (Resolvable<T>) responseResolvable;
    }

    public void shutdown() {
        connection.close();
    }

    public boolean isListening() {
        return connection.isListening();
    }

    @Override
    public void dataReceived(DhtEvent event) {
        try {
            TransportableAction action = actionFactory.createTransportableAction(event);
            action.setSourcePeer(this);

            if(action instanceof ResponseAction) {
                ResponseAction responseAction = (ResponseAction) action;
                Resolvable<ResponseAction> responseResolvable;
                synchronized (pendingRequests) {
                    responseResolvable = pendingRequests.remove(responseAction.getRequestId());
                }
                if(responseResolvable != null) {
                    responseResolvable.resolve(responseAction);
                } else {
                    LOG.warn("received ResponseAction without a tracked request!");
                }
            } else {
                dispatcher.dispatch(action);
            }
        } catch (IOException e) {
            LOG.warn("receive failure: " + e.getMessage());
            shutdown();
        }
    }

    @Override
    public void listenerDisrupted() {
        dispatcher.dispatch(new PeerDisconnected(this));
    }

    @Override
    public String toString() {
        return String.format("peerId: %s type: %s", id, type);
    }
}
