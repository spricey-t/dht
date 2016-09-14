package com.virohtus.dht.event;

import com.virohtus.dht.utils.DhtUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class Event {

    private static final Logger LOG = LoggerFactory.getLogger(Event.class);
    protected DhtUtilities dhtUtilities = new DhtUtilities();

    private String initiatingNodeId;

    public Event(String initiatingNodeId) {
        this.initiatingNodeId = initiatingNodeId;
    }

    public Event(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)
        ) {
            deserialize(dataInputStream);
        }
    }

    public abstract int getType();

    public String getInitiatingNodeId() {
        return initiatingNodeId;
    }

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
        dhtUtilities.writeString(initiatingNodeId, dataOutputStream);
    }

    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        int type = dataInputStream.readInt();
        if (type != getType()) {
            throw new IllegalArgumentException("cannot construct event: " + getClass().getName() +
                    " Unmatched event types, received: " + type + " expected: " + getType());
        }
        initiatingNodeId = dhtUtilities.readString(dataInputStream);
    }
}
