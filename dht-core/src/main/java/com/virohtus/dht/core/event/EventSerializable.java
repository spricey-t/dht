package com.virohtus.dht.core.event;

import java.io.IOException;

public interface EventSerializable {
    byte[] getBytes() throws IOException;
}
