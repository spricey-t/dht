package com.virohtus.dht.transport.serialize;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TransportInputStream extends DataInputStream {

    public TransportInputStream(InputStream in) {
        super(in);
    }

    public byte[] readSizedData() throws IOException {
        int dataLength = readInt();
        byte[] data = new byte[dataLength];
        readFully(data);
        return data;
    }
}
