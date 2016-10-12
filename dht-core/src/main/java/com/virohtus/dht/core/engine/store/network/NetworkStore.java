package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.network.peer.Peer;

import java.io.IOException;
import java.net.SocketAddress;

public class NetworkStore implements Store {

    private final PeerStore peerStore;

    public NetworkStore(PeerStore peerStore) {
        this.peerStore = peerStore;
    }

    public void joinNetwork(SocketAddress socketAddress) throws IOException {
        Peer peer = peerStore.createPeer(socketAddress);
    }

    @Override
    public void onAction(Action action) {
    }

}
