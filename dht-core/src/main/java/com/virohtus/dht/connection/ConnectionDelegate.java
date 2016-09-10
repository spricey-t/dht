package com.virohtus.dht.connection;

import java.io.IOException;

public interface ConnectionDelegate {
    void dataReceived(byte[] data);
    void receiveDisrupted(IOException e);
}
