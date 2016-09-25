package com.virohtus.dht.core.key.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;

public class SplitKeyspaceRequest extends Event {

    public SplitKeyspaceRequest() {
    }

    public SplitKeyspaceRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.SPLIT_KEYSPACE_REQUEST;
    }

}
