package com.virohtus.dht.core.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.network.event.GetDhtNetwork;
import com.virohtus.dht.core.network.event.NodeIdentityRequest;
import com.virohtus.dht.core.network.event.NodeIdentityResponse;
import com.virohtus.dht.core.network.event.SetPredecessorRequest;
import com.virohtus.dht.core.peer.event.PeerDisconnected;
import com.virohtus.dht.core.util.DhtInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EventFactory {

    private static final Logger LOG = LoggerFactory.getLogger(EventFactory.class);
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
            case DhtProtocol.NODE_IDENTITY_REQUEST: return new NodeIdentityRequest(data);
            case DhtProtocol.NODE_IDENTITY_RESPONSE: return new NodeIdentityResponse(data);
            case DhtProtocol.SET_PREDECESSOR_REQUEST: return new SetPredecessorRequest(data);
            case DhtProtocol.GET_DHT_NETWORK: return new GetDhtNetwork(data);
        }

        LOG.error("received unsupported event type: " + eventType);
        throw new UnsupportedEventTypeException(eventType);
    }
}
