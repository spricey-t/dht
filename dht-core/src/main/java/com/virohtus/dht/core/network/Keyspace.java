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
    private final Object lock;
    private int start;
    private int end;

    public Keyspace() {
        lock = new Object();
        start = 0;
        end = DhtProtocol.GLOBAL_KEYSPACE;
    }

    public Keyspace(int start, int end) {
        this();
        this.start = start;
        this.end = end;
    }

    public Keyspace(DhtInputStream inputStream) throws IOException {
        this();
        fromWire(inputStream);
    }

    public int getStart() {
        synchronized (lock) {
            return start;
        }
    }

    public void setStart(int start) {
        synchronized (lock) {
            this.start = start;
        }
    }

    public int getEnd() {
        synchronized (lock) {
            return end;
        }
    }

    public void setEnd(int end) {
        synchronized (lock) {
            this.end = end;
        }
    }

    public boolean inKeyspace(int key) {
        synchronized (lock) {
            return key > start && key <= end;
        }
    }

    public Keyspace split() {
        synchronized (lock) {
            int mid = (end - start) / 2;
            int originalStart = start;
            start = mid;
            return new Keyspace(originalStart, mid);
        }
    }

    public void merge(Keyspace keyspace) {
        synchronized (lock) {
            if(start > keyspace.getEnd()) {
                this.start = keyspace.getStart();
            } else if(end < keyspace.getStart()) {
                this.end = keyspace.end;
            } else {
                LOG.error("keyspace merge error");
            }
        }
    }

    public boolean isDefaultKeyspace() {
        synchronized (lock) {
            return start == 0 && end == DhtProtocol.GLOBAL_KEYSPACE;
        }
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
