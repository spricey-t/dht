package com.virohtus.dht.transport.serialize;

import java.io.DataInputStream;
import java.io.InputStream;

public class TransportInputStream extends DataInputStream {

    public TransportInputStream(InputStream in) {
        super(in);
    }

}
