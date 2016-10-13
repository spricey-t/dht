package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.ResponseAction;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class JoinNetworkResponse extends ResponseAction {

    public JoinNetworkResponse(String requestId) {
        super(requestId);
    }

    public JoinNetworkResponse(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    @Override
    public int getType() {
        return DhtProtocol.JOIN_NETWORK_RESPONSE;
    }

}
