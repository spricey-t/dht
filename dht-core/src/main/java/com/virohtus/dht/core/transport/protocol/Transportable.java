package com.virohtus.dht.core.transport.protocol;

import java.io.IOException;

public interface Transportable {
    byte[] serialize() throws IOException;
    void deserialize(byte[] data) throws IOException;
}
