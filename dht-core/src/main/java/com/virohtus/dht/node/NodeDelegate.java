package com.virohtus.dht.node;

public interface NodeDelegate extends PeerDelegate {
    void peerConnected(Peer peer);
    void onNetworkJoin(Peer peer);
}
