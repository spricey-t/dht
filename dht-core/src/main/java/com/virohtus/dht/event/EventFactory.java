package com.virohtus.dht.event;

public class EventFactory {

    private static final EventFactory instance = new EventFactory();

    private EventFactory() {}

    public static EventFactory getInstance() {
        return instance;
    }


    public Event createEvent(byte[] data) throws UnsupportedEventException {
        throw new UnsupportedEventException("");
    }
}
