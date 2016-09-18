package com.virohtus.dht.core.event;

public interface EventHandler {
    void handle(String peerId, Event event);
}
