package com.virohtus.dht.core.util;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.EventSerializable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DhtOutputStream extends DataOutputStream {

    public DhtOutputStream(OutputStream out) {
        super(out);
    }

    public void writeString(String str) throws IOException {
        byte[] data = str.getBytes(DhtProtocol.STRING_ENCODING);
        writeSizedData(data);
    }

    public void writeSizedData(byte[] data) throws IOException {
        writeInt(data.length);
        write(data);
    }

    public void writeEventSerializable(EventSerializable eventSerializable) throws IOException {
        writeSizedData(eventSerializable.getBytes());
    }

    public void writeNullableEventSerializable(EventSerializable nullableEventSerializable) throws IOException {
        writeBoolean(nullableEventSerializable != null);
        if(nullableEventSerializable != null) {
            writeEventSerializable(nullableEventSerializable);
        }
    }
}
