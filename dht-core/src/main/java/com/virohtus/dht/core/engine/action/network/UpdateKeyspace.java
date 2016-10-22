package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.network.Keyspace;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class UpdateKeyspace extends TransportableAction {

    private NodeIdentity nodeIdentity;
    private Keyspace keyspace;

    public UpdateKeyspace(NodeIdentity nodeIdentity, Keyspace keyspace) {
        this.nodeIdentity = nodeIdentity;
        this.keyspace = keyspace;
    }

    public UpdateKeyspace(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    public NodeIdentity getNodeIdentity() {
        return nodeIdentity;
    }

    public Keyspace getKeyspace() {
        return keyspace;
    }

    @Override
    public int getType() {
        return DhtProtocol.UPDATE_KEYSPACE;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        super.toWire(outputStream);
        nodeIdentity.toWire(outputStream);
        keyspace.toWire(outputStream);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        super.fromWire(inputStream);
        nodeIdentity = new NodeIdentity(inputStream);
        keyspace = new Keyspace(inputStream);
    }
}
