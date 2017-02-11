package com.virohtus.dht.transport;

public abstract class Event {
    public Event() {}
    public Event(byte[] data) {}
    public abstract String getKey();
    public abstract byte[] serialize();
}
