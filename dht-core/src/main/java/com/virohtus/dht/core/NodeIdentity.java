package com.virohtus.dht.core;

import java.net.SocketAddress;

public class NodeIdentity {

    private String nodeId;
    private SocketAddress socketAddress;

    public NodeIdentity(String nodeId, SocketAddress socketAddress) {
        this.nodeId = nodeId;
        this.socketAddress = socketAddress;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
}
