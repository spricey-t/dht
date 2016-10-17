package com.virohtus.dht.core.network;

import com.virohtus.dht.core.action.Wireable;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Network implements Wireable {

    private final List<Node> nodes;

    public Network(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Network(DhtInputStream inputStream) throws IOException {
        nodes = new ArrayList<>();
        fromWire(inputStream);
    }

    public List<Node> getNodes() {
        synchronized (nodes) {
            return new ArrayList<>(nodes);
        }
    }

    public void addNode(Node node) {
        synchronized (nodes) {
            nodes.add(node);
        }
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        synchronized (nodes) {
            outputStream.writeInt(nodes.size());
            for (Node node : nodes) {
                node.toWire(outputStream);
            }
        }
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        synchronized (nodes) {
            int nodecount = inputStream.readInt();
            for(int i = 0; i < nodecount; i++) {
                nodes.add(new Node(inputStream));
            }
        }
    }
}
