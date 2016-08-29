package com.virohtus.dht.overlay.node;

import com.virohtus.dht.event.Event;

public interface ConnectionDelegate {
    void onEvent(Event event);
}
