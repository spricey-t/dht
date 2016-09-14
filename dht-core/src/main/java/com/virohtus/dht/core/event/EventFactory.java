package com.virohtus.dht.core.event;

import com.virohtus.dht.core.util.DhtInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EventFactory {

    private static final EventFactory instance = new EventFactory();

    private EventFactory() {}

    public static EventFactory getInstance() {
        return instance;
    }

    public Event createEvent(byte[] data) throws IOException, UnsupportedEventTypeException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream);

        int eventType = inputStream.readInt();
        switch (eventType) {
        }

        throw new UnsupportedEventTypeException(eventType);
    }
}
