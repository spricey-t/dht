package com.virohtus.dht.core.event;

public interface Wireable {
    void read(byte[] data);
    byte[] write();
}
