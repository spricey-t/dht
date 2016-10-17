package com.virohtus.dht.core.network;

import com.virohtus.dht.core.action.Wireable;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.IOException;

public class Node implements Wireable {

    private NodeIdentity nodeIdentity;
    private Keyspace keyspace;
    private FingerTable fingerTable;

    public Node(NodeIdentity nodeIdentity, Keyspace keyspace, FingerTable fingerTable) {
        this.nodeIdentity = nodeIdentity;
        this.keyspace = keyspace;
        this.fingerTable = fingerTable;
    }

    public Node(Node copy) {
        this.nodeIdentity = new NodeIdentity(copy.getNodeIdentity());
        this.keyspace = new Keyspace(copy.getKeyspace());
        this.fingerTable = new FingerTable(copy.getFingerTable());
    }

    public Node(DhtInputStream inputStream) throws IOException {
        fromWire(inputStream);
    }

    public NodeIdentity getNodeIdentity() {
        return nodeIdentity;
    }

    public Keyspace getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(Keyspace keyspace) {
        this.keyspace = keyspace;
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }

    public void setFingerTable(FingerTable fingerTable) {
        this.fingerTable = fingerTable;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        nodeIdentity.toWire(outputStream);
        keyspace.toWire(outputStream);
        fingerTable.toWire(outputStream);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        nodeIdentity = new NodeIdentity(inputStream);
        keyspace = new Keyspace(inputStream);
        fingerTable = new FingerTable(inputStream);
    }
}
