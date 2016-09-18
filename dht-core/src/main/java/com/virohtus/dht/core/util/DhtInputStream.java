package com.virohtus.dht.core.util;

import com.virohtus.dht.core.DhtProtocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DhtInputStream extends DataInputStream {

    public DhtInputStream(InputStream in) {
        super(in);
    }

    public String readString() throws IOException {
        return new String(readSizedData(), DhtProtocol.STRING_ENCODING);
    }

    public byte[] readSizedData() throws IOException {
        int dataLength = readInt();
        byte[] data = new byte[dataLength];
        readFully(data);
        return data;
    }
}
