package com.virohtus.dht.event;

public class UnsupportedEventException extends Exception {
    private final int eventType;

    public UnsupportedEventException(int eventType) {
        super("Unsupported event type: " + eventType);
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }
}
