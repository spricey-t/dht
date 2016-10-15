package com.virohtus.dht.core.action;

import com.virohtus.dht.core.engine.action.network.*;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ActionFactory {

    private static final ActionFactory instance = new ActionFactory();

    private ActionFactory() {}

    public static ActionFactory getInstance() {
        return instance;
    }

    public TransportableAction createTransportableAction(DhtEvent event) throws IOException {
        int type;
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.getPayload());
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            type = inputStream.readInt();
        }

        switch (type) {
            case DhtProtocol.JOIN_NETWORK_REQUEST: return new JoinNetworkRequest(event);
            case DhtProtocol.JOIN_NETWORK_RESPONSE: return new JoinNetworkResponse(event);
            case DhtProtocol.GET_NODE_IDENTITY_REQUEST: return new GetNodeIdentityRequest(event);
            case DhtProtocol.GET_NODE_IDENTITY_RESPONSE: return new GetNodeIdentityResponse(event);
            case DhtProtocol.GET_NETWORK: return new GetNetwork(event);
        }

        throw new IOException("unsupported TransportableAction type: " + type);
    }
}
