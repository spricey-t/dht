package com.virohtus.dht.core.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NodeNetwork {

    private static final Logger LOG = LoggerFactory.getLogger(NodeNetwork.class);
    private NodeIdentity predecessor;
    private final List<NodeIdentity> successors;
    private final Object lock;

    public NodeNetwork() {
        predecessor = null;
        successors = new ArrayList<>();
        lock = new Object();
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return predecessor == null && successors.isEmpty();
        }
    }

    public void setPredecessor(NodeIdentity nodeIdentity) {
        synchronized (lock) {
            predecessor = nodeIdentity;
        }
    }

    public Optional<NodeIdentity> getPredecessor() {
        return Optional.ofNullable(predecessor);
    }

    public void addSuccessor(NodeIdentity nodeIdentity) {
        synchronized (lock) {
            successors.add(nodeIdentity);
        }
    }
}
