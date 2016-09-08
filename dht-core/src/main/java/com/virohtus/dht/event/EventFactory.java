package com.virohtus.dht.event;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {

    private static final EventFactory instance = new EventFactory();

    private EventFactory() {}

    public static EventFactory getInstance() {
        return instance;
    }


    public Event createEvent(byte[] data) throws UnsupportedEventException, IOException {
        int eventType = -1;
        DataInputStream dataInputStream = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            dataInputStream = new DataInputStream(byteArrayInputStream);
            eventType = dataInputStream.readInt();
        } finally {
            if(dataInputStream != null) {
                dataInputStream.close();
            }
        }

        switch (eventType) {
            case EventProtocol.ERROR_EVENT: return new ErrorEvent(data);
        }

        throw new UnsupportedEventException("unsupported event type: " + eventType);
    }
}
