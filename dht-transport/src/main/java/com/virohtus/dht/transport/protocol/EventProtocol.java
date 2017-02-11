package com.virohtus.dht.transport.protocol;

import java.io.IOException;

public interface EventProtocol {

    int V1 = 1;

    int getVersion();
    byte[] generateHeaders() throws IOException;
}
