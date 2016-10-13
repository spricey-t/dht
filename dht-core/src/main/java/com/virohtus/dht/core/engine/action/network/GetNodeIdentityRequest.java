package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.RequestAction;
import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class GetNodeIdentityRequest extends RequestAction {

    public GetNodeIdentityRequest() {
    }

    public GetNodeIdentityRequest(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_NODE_IDENTITY_REQUEST;
    }

}
