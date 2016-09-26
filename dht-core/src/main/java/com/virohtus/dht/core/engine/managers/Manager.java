package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.event.Event;

public interface Manager {
    void handle(String peerId, Event event);
}
