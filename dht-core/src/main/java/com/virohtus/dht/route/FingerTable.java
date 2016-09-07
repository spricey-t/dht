package com.virohtus.dht.route;

import com.virohtus.dht.overlay.node.OverlayNodeConnectionInfo;
import com.virohtus.dht.event.EventSerializable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FingerTable implements EventSerializable {

    private List<OverlayNodeConnectionInfo> fingers;

    public FingerTable() {
        fingers = new ArrayList<>();
    }

    public FingerTable(DataInputStream dataInputStream) throws IOException {
        int fingerCount = dataInputStream.readInt();
        for(int i = 0; i < fingerCount; i++) {
            fingers.add(new OverlayNodeConnectionInfo(dataInputStream));
        }
    }

    public OverlayNodeConnectionInfo getFinger(int n) {
        synchronized (fingers) {
            return fingers.get(n);
        }
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
        ) {
            synchronized (fingers) {
                dataOutputStream.writeInt(fingers.size());
                for (OverlayNodeConnectionInfo finger : fingers) {
                    dataOutputStream.write(finger.serialize());
                }
            }
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }
}
