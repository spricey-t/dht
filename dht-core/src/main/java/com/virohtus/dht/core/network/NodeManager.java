package com.virohtus.dht.core.network;

import java.net.SocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class NodeManager {

    private final Node node;

    public NodeManager(Node node) {
        this.node = node;
    }

    public Node getCurrentNode() {
        synchronized (node) {
            return new Node(node);
        }
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        synchronized (node) {
            node.getNodeIdentity().setSocketAddress(socketAddress);
        }
    }

    public void setKeyspace(Keyspace keyspace) {
        synchronized (node) {
            node.setKeyspace(keyspace);
        }
    }

    public void mergeKeyspace(Keyspace keyspace) {
        synchronized (node) {
            node.getKeyspace().merge(keyspace);
        }
    }

    public void setFingerTable(FingerTable fingerTable) {
        synchronized (node) {
            node.setFingerTable(fingerTable);
        }
    }

    public void setPredecessor(Node predecessor) {
        synchronized (node) {
            node.getFingerTable().setPredecessor(predecessor);
        }
    }

    public void setImmediateSuccessor(Node successor) {
        synchronized (node) {
            node.getFingerTable().setImmediateSuccessor(successor);
        }
    }

    public void removeSuccessor(NodeIdentity successor) {
        synchronized (node) {
            node.getFingerTable().removeSuccessor(successor);
        }
    }
}
