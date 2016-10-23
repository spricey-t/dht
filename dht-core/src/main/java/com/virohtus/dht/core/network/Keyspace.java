package com.virohtus.dht.core.network;

import com.virohtus.dht.core.action.Wireable;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Keyspace implements Wireable {

    private static final Logger LOG = LoggerFactory.getLogger(Keyspace.class);
    private int offset;
    private int length;

    public Keyspace() {
        offset = 0;
        length = DhtProtocol.GLOBAL_KEYSPACE;
    }

    public Keyspace(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public Keyspace(Keyspace other) {
        this();
        this.offset = other.getOffset();
        this.length = other.getLength();
    }

    public Keyspace(DhtInputStream inputStream) throws IOException {
        this();
        fromWire(inputStream);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getEffectiveEnd() {
        int effectiveEnd = offset + length;
        if(isWrapped()) {
            effectiveEnd = effectiveEnd - DhtProtocol.GLOBAL_KEYSPACE;
        }
        return effectiveEnd;
    }

    public boolean isWrapped() {
        return (offset + length) > DhtProtocol.GLOBAL_KEYSPACE;
    }

    public boolean inKeyspace(int key) {
        if(isWrapped()) {
            return (key > offset && key <= DhtProtocol.GLOBAL_KEYSPACE) || (key <= getEffectiveEnd());
        }
        return key > offset && key <= getEffectiveEnd();
    }

    public Keyspace[] split() {
        int newLength = length / 2;
        int newOffset = offset + newLength;

        Keyspace[] split = new Keyspace[2];
        split[0] = new Keyspace(offset, newLength);
        split[1] = new Keyspace(newOffset, newLength + (length % 2 == 0 ? 0 : 1));
        return split;
    }

    public void merge(Keyspace keyspace) {
        int thisEnd = offset + length;
        int otherEnd = keyspace.getOffset() + keyspace.getLength();
        int newEnd = Math.max(thisEnd, otherEnd);
        int newOffset = Math.min(offset, keyspace.getOffset());
        int newLength = newEnd - newOffset;
        this.offset = newOffset;
        this.length = newLength;
    }

    public boolean isDefaultKeyspace() {
        return offset == 0 && length == DhtProtocol.GLOBAL_KEYSPACE;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        outputStream.writeInt(offset);
        outputStream.writeInt(length);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        offset = inputStream.readInt();
        length = inputStream.readInt();
    }
}
