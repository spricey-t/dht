package com.virohtus.dht.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class Event {
    private static final Logger LOG = LoggerFactory.getLogger(Event.class);

    public Event() {
    }

    public Event(byte[] data) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        try {
            deserialize(dataInputStream);
        } finally {
            dataInputStream.close();
        }
    }

    public abstract int getType();

    public byte[] getData() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            serialize(dataOutputStream);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } finally {
            dataOutputStream.close();
        }
    }

    protected void deserialize(final DataInputStream dataInputStream) throws IOException {
        int type = dataInputStream.readInt();
        if(type != getType()) {
            String message = String.format("Can not construct Event: %s. Expected EventType: %d but received: %d",
                    getClass().getName(), getType(), type);
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    protected void serialize(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(getType());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return getType();
    }
}
