package com.virohtus.dht.event;

import com.virohtus.dht.node.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {

    private static final Logger LOG = LoggerFactory.getLogger(EventFactory.class);
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
            case EventProtocol.GET_OVERLAY: return new GetOverlay(data);

            case EventProtocol.PEER_DETAILS_REQUEST: return new PeerDetailsRequest(data);
            case EventProtocol.PEER_DETAILS_RESPONSE: return new PeerDetailsResponse(data);

            case EventProtocol.FINGER_TABLE_REQUEST: return new FingerTableRequest(data);
            case EventProtocol.FINGER_TABLE_RESPONSE: return new FingerTableResponse(data);

            case EventProtocol.GET_PREDECESSOR_REQUEST: return new GetPredecessorRequest(data);
            case EventProtocol.GET_PREDECESSOR_RESPONSE: return new GetPredecessorResponse(data);
            case EventProtocol.SET_PREDECESSOR_REQUEST: return new SetPredecessorRequest(data);
        }

        LOG.error("could not create event with type: " + type);
        throw new UnsupportedEventException(type);
    }
}
