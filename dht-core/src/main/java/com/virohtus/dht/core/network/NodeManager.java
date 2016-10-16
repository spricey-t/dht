package com.virohtus.dht.core.network;

import java.net.SocketAddress;

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

    public Network getNetwork() {
        return null;
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

    public void setFingerTable(FingerTable fingerTable) {
        synchronized (node) {
            node.setFingerTable(fingerTable);
        }
    }
}
