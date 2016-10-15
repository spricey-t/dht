package com.virohtus.dht.core.network;

import com.virohtus.dht.core.action.Wireable;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FingerTable implements Wireable {

    private final List<Node> successors;
    private final Object predecessorLock;
    private Node predecessor;

    public FingerTable() {
        successors = new ArrayList<>();
        predecessorLock = new Object();
        predecessor = null;
    }

    public FingerTable(DhtInputStream inputStream) throws IOException {
        this();
        fromWire(inputStream);
    }

    public Node getPredecessor() {
        synchronized (predecessorLock) {
            return predecessor;
        }
    }

    public void setPredecessor(Node predecessor) {
        synchronized (predecessorLock) {
            this.predecessor = predecessor;
        }
    }

    public List<Node> getSuccessors() {
        synchronized (successors) {
            return successors;
        }
    }

    public boolean hasSuccessors() {
        synchronized (successors) {
            return !successors.isEmpty();
        }
    }

    public void addSuccessor(Node successor) {
        synchronized (successors) {
            successors.add(successor);
        }
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        synchronized (successors) {
            outputStream.writeInt(successors.size());
            for (Node successor : successors) {
                successor.toWire(outputStream);
            }
        }
        synchronized (predecessorLock) {
            boolean predecessorExists = predecessor != null;
            outputStream.writeBoolean(predecessorExists);
            if (predecessorExists) {
                predecessor.toWire(outputStream);
            }
        }
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        synchronized (successors) {
            int successorCount = inputStream.readInt();
            for (int i = 0; i < successorCount; i++) {
                successors.add(new Node(inputStream));
            }
        }
        synchronized (predecessorLock) {
            if (inputStream.readBoolean()) {
                predecessor = new Node(inputStream);
            }
        }
    }
}
