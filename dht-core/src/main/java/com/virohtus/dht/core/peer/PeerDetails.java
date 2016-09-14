package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.event.EventSerializable;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PeerDetails implements EventSerializable {
    private String nodeId;
    private ConnectionInfo connectionInfo;

    public PeerDetails(String nodeId, ConnectionInfo connectionInfo) {
        this.nodeId = nodeId;
        this.connectionInfo = connectionInfo;
    }

    public PeerDetails(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream);
        ) {
            nodeId = inputStream.readString();
            connectionInfo = new ConnectionInfo(inputStream.readSizedData());
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream);
        ) {
            outputStream.writeString(nodeId);
            outputStream.writeSizedData(connectionInfo.getBytes());
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public String getNodeId() {
        return nodeId;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }
}
