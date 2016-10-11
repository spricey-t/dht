package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

public class GetNodeIdentityRequest extends TransportableAction {

    @Override
    public int getType() {
        return DhtProtocol.GET_NODE_IDENTITY_REQUEST;
    }

}
