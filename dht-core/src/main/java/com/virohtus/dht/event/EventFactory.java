package com.virohtus.dht.event;

import com.virohtus.dht.overlay.transport.ConnectionError;
import com.virohtus.dht.overlay.transport.tcp.ReceiveError;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {

    private static final EventFactory instance = new EventFactory();

    private EventFactory() {}

    public static EventFactory getInstance() {
        return instance;
    }


    public Event createEvent(byte[] data) throws UnsupportedEventException, IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        int eventType = dataInputStream.readInt();

        switch (eventType) {
            case EventProtocol.ERROR_EVENT: return new ErrorEvent(data);
            case EventProtocol.CONNECTION_ERROR: return new ConnectionError(data);
            case EventProtocol.RECEIVER_ERROR: return new ReceiveError(data);
            case EventProtocol.STRING_MESSAGE_EVENT: return new StringMessageEvent(data);
        }

        throw new UnsupportedEventException("unsupported event type: " + eventType);
    }
}
