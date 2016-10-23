package com.virohtus.dht.core.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.List;

public class NodeManager {

    private static final Logger LOG = LoggerFactory.getLogger(NodeManager.class);
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

    public void setSuccessors(List<Node> newSuccessors) {
        synchronized (node) {
            node.getFingerTable().setSuccessors(newSuccessors);
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

    public void updateImmediateSuccessor(Node successor) {
        synchronized (node) {
            int index = node.getFingerTable().getIndexOfFinger(successor);
            if(index <= 0) {
                node.getFingerTable().updateFinger(index, successor);
            } else {
                LOG.error("tried to update immediate successor but it is not in the fingertable!");
            }
        }
    }

    public void removeSuccessor(NodeIdentity successor) {
        synchronized (node) {
            node.getFingerTable().removeSuccessor(successor);
        }
    }
}
