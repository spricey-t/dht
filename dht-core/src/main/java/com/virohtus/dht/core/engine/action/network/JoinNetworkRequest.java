package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class JoinNetworkRequest extends TransportableAction {

    public JoinNetworkRequest() {
    }

    public JoinNetworkRequest(DhtEvent event) throws IOException {
        super(event);
    }

    @Override
    public int getType() {
        return DhtProtocol.JOIN_NETWORK_REQUEST;
    }
}
