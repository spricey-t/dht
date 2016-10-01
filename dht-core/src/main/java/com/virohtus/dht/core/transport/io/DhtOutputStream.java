package com.virohtus.dht.core.transport.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DhtOutputStream extends DataOutputStream {

    public DhtOutputStream(OutputStream out) {
        super(out);
    }

    public void writeSizedData(byte[] data) throws IOException {
        writeInt(data.length);
        write(data);
    }
}
