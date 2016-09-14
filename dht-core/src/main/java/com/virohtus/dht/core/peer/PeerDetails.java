package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.transport.connection.ConnectionInfo;

public class PeerDetails {
    private String nodeId;
    private ConnectionInfo connectionInfo;

    public PeerDetails(String nodeId, ConnectionInfo connectionInfo) {
        this.nodeId = nodeId;
        this.connectionInfo = connectionInfo;
    }

    public String getNodeId() {
        return nodeId;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }
}
