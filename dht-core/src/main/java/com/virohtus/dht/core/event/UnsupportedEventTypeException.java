package com.virohtus.dht.core.event;

public class UnsupportedEventTypeException extends Exception {

    private final int eventType;

    public UnsupportedEventTypeException(int eventType) {
        super("unsupported event type: " + eventType);
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }
}
