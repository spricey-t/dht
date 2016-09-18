package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;

public class GetPredecessorRequest extends Event {

    public GetPredecessorRequest() {
    }

    public GetPredecessorRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_PREDECESSOR_REQUEST;
    }

}
