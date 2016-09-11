package com.virohtus.dht.event;

import com.virohtus.dht.connection.event.ConnectionDetailsRequest;
import com.virohtus.dht.connection.event.ConnectionDetailsResponse;
import com.virohtus.dht.node.event.GetOverlay;
import com.virohtus.dht.node.event.PeerDetailsRequest;
import com.virohtus.dht.node.event.PeerDetailsResponse;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {

    private static final EventFactory instance = new EventFactory();

    private EventFactory() {}

    public static EventFactory getInstance() {
        return instance;
    }

    public Event createEvent(byte[] data) throws IOException, UnsupportedEventException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        int type = dataInputStream.readInt();

        switch(type) {
            case EventProtocol.CONNECTION_DETAILS_REQUEST: return new ConnectionDetailsRequest(data);
            case EventProtocol.CONNECTION_DETAILS_RESPONSE: return new ConnectionDetailsResponse(data);
            case EventProtocol.GET_OVERLAY: return new GetOverlay(data);

            case EventProtocol.PEER_DETAILS_REQUEST: return new PeerDetailsRequest(data);
            case EventProtocol.PEER_DETAILS_RESPONSE: return new PeerDetailsResponse(data);
        }

        throw new UnsupportedEventException(type);
    }
}
