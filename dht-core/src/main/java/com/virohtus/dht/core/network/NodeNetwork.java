package com.virohtus.dht.core.network;

import com.virohtus.dht.core.network.peer.Peer;

import java.util.ArrayList;
import java.util.List;

public class NodeNetwork {

    private final Object predecessorLock;
    private final List<Peer> successors;
    private Peer predecessor;

    public NodeNetwork() {
        predecessorLock = new Object();
        successors = new ArrayList<>();
        predecessor = null;
    }

    public Peer getPredecessor() {
        synchronized (predecessorLock) {
            return predecessor;
        }
    }

    public void setPredecessor(Peer predecessor) {
        synchronized (predecessorLock) {
            this.predecessor = predecessor;
        }
    }

}
