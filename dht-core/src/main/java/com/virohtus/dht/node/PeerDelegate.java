package com.virohtus.dht.node;

import com.virohtus.dht.event.Event;

public interface PeerDelegate {
    void peerEventReceived(Peer peer, Event event);
    void peerDisconnected(Peer peer);
}
