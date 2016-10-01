package com.virohtus.dht.core.event;

/*
import com.virohtus.dht.core.transport.DhtInputStream;
import com.virohtus.dht.core.transport.DhtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class Event {

    public Event() {}

    public Event(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            deserialize(inputStream);
        }
    }

    public abstract int getType();

    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream)
        ) {
            serialize(outputStream);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public void serialize(DhtOutputStream outputStream) throws IOException {
        outputStream.writeInt(getType());
    }

    public void deserialize(DhtInputStream inputStream) throws IOException {
        int receivedType = inputStream.readInt();
        if(receivedType != getType()) {
            throw new IllegalArgumentException(String.format("received illegal EventType: %d when deserializing Event: %s", receivedType, getClass().getName()));
        }
    }
}
*/
