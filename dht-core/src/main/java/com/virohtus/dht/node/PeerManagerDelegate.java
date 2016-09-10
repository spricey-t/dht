package com.virohtus.dht.node;

public interface PeerManagerDelegate {
    void peerDisconnected(Peer peer);
}
