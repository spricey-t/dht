package com.virohtus.dht.core.network;

import com.virohtus.dht.core.action.Wireable;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class FingerTable implements Wireable {

    private final List<Node> successors;
    private Node predecessor;

    public FingerTable() {
        successors = new ArrayList<>();
        predecessor = null;
    }

    public FingerTable(DhtInputStream inputStream) throws IOException {
        this();
        fromWire(inputStream);
    }

    public Node getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    public List<Node> getSuccessors() {
        return successors;
    }

    public boolean hasSuccessors() {
        return !successors.isEmpty();
    }

    public void addSuccessor(Node successor) {
        successors.add(successor);
    }

    public Node getImmediateSuccessor() {
        if(!hasSuccessors()) {
            return null;
        }
        return successors.get(0);
    }

    public void setImmediateSuccessor(Node node) {
        successors.add(0, node);
    }

    public Node removeSuccessor(NodeIdentity nodeIdentity) {
        Node node = null;
        Iterator<Node> nodeIterator = successors.iterator();
        while(nodeIterator.hasNext()) {
            Node n = nodeIterator.next();
            if(n.getNodeIdentity().equals(nodeIdentity)) {
                nodeIterator.remove();
                node = n;
            }
        }
        return node;
    }

    public Optional<Node> getSuccessor(NodeIdentity nodeIdentity) {
        return successors.stream().filter(successor -> successor.getNodeIdentity().equals(nodeIdentity)).findAny();
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        outputStream.writeInt(successors.size());
        for (Node successor : successors) {
            successor.toWire(outputStream);
        }
        boolean predecessorExists = predecessor != null;
        outputStream.writeBoolean(predecessorExists);
        if (predecessorExists) {
            predecessor.toWire(outputStream);
        }
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        int successorCount = inputStream.readInt();
        for (int i = 0; i < successorCount; i++) {
            successors.add(new Node(inputStream));
        }
        if (inputStream.readBoolean()) {
            predecessor = new Node(inputStream);
        }
    }
}
