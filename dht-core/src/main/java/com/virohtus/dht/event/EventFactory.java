package com.virohtus.dht.event;

import com.virohtus.dht.overlay.transport.ConnectionError;
import com.virohtus.dht.overlay.transport.tcp.ReceiveError;
import com.virohtus.dht.route.event.RequestFingerTableEvent;

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
            case EventProtocol.HEARTBEAT_EVENT: return new HeartbeatEvent(data);
            case EventProtocol.REQUEST_FINGER_TABLE_EVENT: return new RequestFingerTableEvent(data);
            case EventProtocol.ERROR_EVENT: return new ErrorEvent(data);
            case EventProtocol.CONNECTION_ERROR: return new ConnectionError(data);
            case EventProtocol.RECEIVER_ERROR: return new ReceiveError(data);
            case EventProtocol.STRING_MESSAGE_EVENT: return new StringMessageEvent(data);
        }

        throw new UnsupportedEventException("unsupported event type: " + eventType);
    }
}
