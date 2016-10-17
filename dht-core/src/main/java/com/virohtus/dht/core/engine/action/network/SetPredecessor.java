package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class SetPredecessor extends TransportableAction {

    private Node node;

    public SetPredecessor(Node node) {
        this.node = node;
    }

    public SetPredecessor(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    public Node getNode() {
        return node;
    }

    @Override
    public int getType() {
        return DhtProtocol.SET_PREDECESSOR;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        super.toWire(outputStream);
        node.toWire(outputStream);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        super.fromWire(inputStream);
        node = new Node(inputStream);
    }
}
