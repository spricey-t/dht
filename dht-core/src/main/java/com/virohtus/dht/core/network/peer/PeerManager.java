package com.virohtus.dht.core.network.peer;

import com.virohtus.dht.core.network.NodeIdentity;

import java.util.Set;

public interface PeerManager {

    Peer getPeer(String peerId) throws PeerNotFoundException;
    Peer getPeer(NodeIdentity nodeIdentity) throws PeerNotFoundException;
    void addPeer(Peer peer);
    void removePeer(Peer peer);
    Set<Peer> getAllPeers();
    boolean isEmpty();
    Set<Peer> clear();
}
