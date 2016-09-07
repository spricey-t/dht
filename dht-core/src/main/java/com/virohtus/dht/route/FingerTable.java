package com.virohtus.dht.route;

import com.virohtus.dht.event.EventSerializable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FingerTable implements EventSerializable {

    private List<FingerTableEntry> fingers;

    public FingerTable() {
        fingers = new ArrayList<>();
    }

    public FingerTable(byte[] data) {
    }

    public FingerTableEntry getFinger(int n) {
        synchronized (fingers) {
            return fingers.get(n);
        }
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            synchronized (fingers) {
                dataOutputStream.writeInt(fingers.size());
                for (FingerTableEntry finger : fingers) {
                    finger.serialize(dataOutputStream);
                }
            }
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } finally {
            dataOutputStream.close();
        }
    }
}
