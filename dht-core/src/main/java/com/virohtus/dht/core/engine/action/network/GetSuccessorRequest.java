package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.RequestAction;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class GetSuccessorRequest extends RequestAction {

    private int distance;
    private Node originatingNode;

    public GetSuccessorRequest(int distance, Node originatingNode) {
        super();
        this.distance = distance;
        this.originatingNode = originatingNode;
    }

    public GetSuccessorRequest(DhtEvent event) throws IOException {
        super(event);
    }

    public int getDistance() {
        return distance;
    }

    public Node getOriginatingNode() {
        return originatingNode;
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_SUCCESSOR_REQUEST;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        super.toWire(outputStream);
        outputStream.writeInt(distance);
        originatingNode.toWire(outputStream);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        super.fromWire(inputStream);
        distance = inputStream.readInt();
        originatingNode = new Node(inputStream);
    }
}
