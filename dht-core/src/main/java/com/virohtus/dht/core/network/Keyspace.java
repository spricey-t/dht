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
    private int start;
    private int end;

    public Keyspace() {
        start = 0;
        end = DhtProtocol.GLOBAL_KEYSPACE;
    }

    public Keyspace(int start, int end) {
        this();
        this.start = start;
        this.end = end;
    }

    public Keyspace(Keyspace other) {
        this.start = other.getStart();
        this.end = other.getEnd();
    }

    public Keyspace(DhtInputStream inputStream) throws IOException {
        this();
        fromWire(inputStream);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean inKeyspace(int key) {
        return key > start && key <= end;
    }

    public Keyspace split() {
        int mid = (end - start) / 2 + start;
        int originalStart = start;
        start = mid;
        return new Keyspace(originalStart, mid);
    }

    public void merge(Keyspace keyspace) {
        if(start > keyspace.getStart()) {
            start = keyspace.getStart();
        }
        if(end < keyspace.getEnd()) {
            end = keyspace.getEnd();
        }
    }

    public boolean isDefaultKeyspace() {
        return start == 0 && end == DhtProtocol.GLOBAL_KEYSPACE;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        outputStream.writeInt(start);
        outputStream.writeInt(end);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        start = inputStream.readInt();
        end = inputStream.readInt();
    }
}
