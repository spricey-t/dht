package com.virohtus.dht.core.network;

import com.virohtus.dht.core.event.EventSerializable;
import com.virohtus.dht.core.key.Keyspace;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NodeResponsibility implements EventSerializable {

    private NodeIdentity nodeIdentity;
    private Keyspace keyspace;

    public NodeResponsibility(NodeIdentity nodeIdentity, Keyspace keyspace) {
        this.nodeIdentity = nodeIdentity;
        this.keyspace = keyspace;
    }

    public NodeResponsibility(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            nodeIdentity = inputStream.readEventSerializable(NodeIdentity.class);
            keyspace = inputStream.readEventSerializable(Keyspace.class);
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream)
        ) {
            outputStream.writeEventSerializable(nodeIdentity);
            outputStream.writeEventSerializable(keyspace);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public NodeIdentity getNodeIdentity() {
        return nodeIdentity;
    }

    public synchronized Keyspace getKeyspace() {
        return keyspace;
    }

    public synchronized void setKeyspace(Keyspace keyspace) {
        this.keyspace = keyspace;
    }

    @Override
    public String toString() {
        return String.format("nodeIdentity: %s, keyspace: %d - %d", nodeIdentity.toString(), keyspace.getStart(), keyspace.getEnd());
    }
}
