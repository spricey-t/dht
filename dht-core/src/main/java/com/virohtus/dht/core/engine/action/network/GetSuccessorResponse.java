package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.ResponseAction;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class GetSuccessorResponse extends ResponseAction {

    private Node node;

    public GetSuccessorResponse(String requestId) {
        super(requestId);
    }

    public GetSuccessorResponse(String requestId, Node node) {
        super(requestId);
        this.node = node;
    }

    public GetSuccessorResponse(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    public Node getNode() {
        return node;
    }

    public boolean hasAnswer() {
        return node != null;
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_SUCCESSOR_RESPONSE;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        super.toWire(outputStream);
        outputStream.writeBoolean(node != null);
        if(node != null) {
            node.toWire(outputStream);
        }
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        super.fromWire(inputStream);
        boolean hasAnswer = inputStream.readBoolean();
        if(hasAnswer) {
            node = new Node(inputStream);
        }
    }
}
