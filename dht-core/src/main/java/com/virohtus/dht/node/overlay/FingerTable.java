package com.virohtus.dht.node.overlay;

import com.virohtus.dht.event.EventSerializable;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FingerTable implements EventSerializable {

    private final DhtUtilities dhtUtilities = new DhtUtilities();
    private Finger predecessor;
    private List<Finger> successors;

    public FingerTable(Finger predecessor, List<Finger> successors) {
        this.predecessor = predecessor;
        this.successors = successors;
    }

    public FingerTable(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        ) {
            boolean hasPredecessor = dataInputStream.readBoolean();
            if (hasPredecessor) {
                predecessor = new Finger(dhtUtilities.readSizedData(dataInputStream));
            }
            int successorSize = dataInputStream.readInt();
            for (int i = 0; i < successorSize; i++) {
                successors.add(new Finger(dhtUtilities.readSizedData(dataInputStream)));
            }
        }
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ) {
            dataOutputStream.writeBoolean(hasPredecessor());
            if (hasPredecessor()) {
                dhtUtilities.writeSizedData(predecessor.serialize(), dataOutputStream);
            }
            dataOutputStream.writeInt(successors.size());
            for (Finger finger : successors) {
                dhtUtilities.writeSizedData(finger.serialize(), dataOutputStream);
            }
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public Finger getPredecessor() {
        return predecessor;
    }

    public List<Finger> getSuccessors() {
        return new ArrayList<>(successors);
    }

    public void setPredecessor(Finger predecessor) {
        this.predecessor = predecessor;
    }

    private boolean hasPredecessor() {
        return predecessor != null;
    }
}
