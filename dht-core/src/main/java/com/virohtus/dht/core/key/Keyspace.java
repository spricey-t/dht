package com.virohtus.dht.core.key;

import com.virohtus.dht.core.event.EventSerializable;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Keyspace implements EventSerializable {

    private long start;
    private long end;

    public Keyspace(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public Keyspace(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            start = inputStream.readLong();
            end = inputStream.readLong();
        }
    }

    public boolean ownsKey(long key) {
        return key > start && key <= end;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream dhtOutputStream = new DhtOutputStream(byteArrayOutputStream);
        ) {
            dhtOutputStream.writeLong(start);
            dhtOutputStream.writeLong(end);
            dhtOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }
}
