package com.virohtus.dht.core.event;

import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class Event {

    private static final Logger LOG = LoggerFactory.getLogger(Event.class);

    public Event() {
    }

    public Event(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream);
        ) {
            deserialize(inputStream);
        }
    }

    public abstract int getType();

    public final byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream);
        ) {
            serialize(outputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        dhtOutputStream.writeInt(getType());
    }

    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        int type = dhtInputStream.readInt();
        if(type != getType()) {
            throw new IllegalArgumentException("tried to deserialize event with unmatching event types: " + type + " " + getType());
        }
    }

}
