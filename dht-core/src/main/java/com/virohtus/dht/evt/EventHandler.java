package com.virohtus.dht.evt;

/**
 * Delegate event handling to this interface.
 * Multiplexing can be done as the implementer sees fit.
 */
public interface EventHandler {
    void onEvent(Event event);
}
