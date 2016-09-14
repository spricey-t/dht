package com.virohtus.dht.core.handler;

import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class HandlerChain implements EventHandler {

    private final List<EventHandler> handlers = new ArrayList<>();

    @Override
    public void handle(Event event) {
        // holds the lock until all handlers have completed
        synchronized (handlers) {
            handlers.stream().forEach(handler -> handler.handle(event));
        }
    }

    public void addHandler(EventHandler handler) {
        synchronized (handlers) {
            handlers.add(handler);
        }
    }

    public void removeHandler(EventHandler handler) {
        synchronized (handlers) {
            handlers.remove(handler);
        }
    }
}
