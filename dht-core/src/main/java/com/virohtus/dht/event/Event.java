package com.virohtus.dht.event;

import java.io.*;

public abstract class Event {

    public Event() {}

    public Event(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)
        ) {
            deserialize(dataInputStream);
        }
    }

    public abstract int getType();

    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ) {
            serialize(dataOutputStream);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(getType());
    }

    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        int type = dataInputStream.readInt();
        if (type != getType()) {
            throw new IllegalArgumentException("cannot construct event: " + getClass().getName() +
                    " Unmatched event types, received: " + type + " expected: " + getType());
        }
    }
}
