package com.virohtus.dht.transport.serialize;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TransportOutputStream extends DataOutputStream {

    public TransportOutputStream(OutputStream out) {
        super(out);
    }

    public void writeSizedData(byte[] data) throws IOException {
        writeInt(data.length);
        write(data);
    }
}
