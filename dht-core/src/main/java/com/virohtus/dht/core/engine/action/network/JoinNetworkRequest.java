package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.RequestAction;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class JoinNetworkRequest extends RequestAction {

    private Node node;

    public JoinNetworkRequest(Node node) {
        super();
        this.node = node;
    }

    public JoinNetworkRequest(DhtEvent event) throws IOException {
        super(event);
    }

    public Node getNode() {
        return node;
    }

    @Override
    public int getType() {
        return DhtProtocol.JOIN_NETWORK_REQUEST;
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
