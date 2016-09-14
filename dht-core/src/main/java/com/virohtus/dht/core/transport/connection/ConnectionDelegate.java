package com.virohtus.dht.core.transport.connection;

import java.io.IOException;

public interface ConnectionDelegate {
    void dataReceived(byte[] data);
    void receiveDisrupted(IOException e);
}
