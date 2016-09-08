package com.virohtus.dht.event;

/**
 * Delegate event handling to this interface.
 * Multiplexing can be done as the implementer sees fit.
 */
public interface EventHandler {
    void onEvent(int connectionId, Event event);
}
