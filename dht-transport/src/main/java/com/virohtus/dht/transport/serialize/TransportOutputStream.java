package com.virohtus.dht.transport.serialize;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;

public class TransportOutputStream extends DataOutputStream {

    public TransportOutputStream(OutputStream out) {
        super(out);
    }

}
