package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.RequestAction;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public class GetPredecessorRequest extends RequestAction {

    public GetPredecessorRequest() {
    }

    public GetPredecessorRequest(DhtEvent event) throws IOException {
        super(event);
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_PREDECESSOR_REQUEST;
    }

}
