package com.virohtus.dht.event;


import java.io.IOException;

public interface EventSerializable {
    byte[] serialize() throws IOException;
}
