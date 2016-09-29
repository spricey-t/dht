package com.virohtus.dht.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DhtInputStream extends DataInputStream {

    public DhtInputStream(InputStream in) {
        super(in);
    }

    public byte[] readSizedData() throws IOException {
        int dataLength = readInt();
        byte[] data = new byte[dataLength];
        readFully(data);
        return data;
    }
}
