package com.virohtus.dht.core.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.peer.event.PeerDetailsRequest;
import com.virohtus.dht.core.peer.event.PeerDetailsResponse;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
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
            case DhtProtocol.PEER_DISCONNECTED: return new PeerDisconnected(data);
            case DhtProtocol.PEER_DETAILS_REQUEST: return new PeerDetailsRequest(data);
            case DhtProtocol.PEER_DETAILS_RESPONSE: return new PeerDetailsResponse(data);
        }

        throw new UnsupportedEventTypeException(eventType);
    }
}
