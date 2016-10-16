package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNodeManager;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.Network;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkStore.class);
    private final DhtNodeManager dhtNodeManager;
    private final PeerStore peerStore;
    private final Resolvable<Network> networkResolvable;
    private final Object networkLock;

    public NetworkStore(DhtNodeManager dhtNodeManager, PeerStore peerStore) {
        this.dhtNodeManager = dhtNodeManager;
        this.peerStore = peerStore;
        this.networkResolvable = new Resolvable<>(DhtProtocol.NETWORK_TIMEOUT);
        this.networkLock = new Object();
    }

    @Override
    public void onAction(Action action) {
    }
}
