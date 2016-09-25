package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;

import java.io.IOException;

public class GetNodeNetworkRequest extends Event {

    public GetNodeNetworkRequest() {
    }

    public GetNodeNetworkRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_NODE_NETWORK_REQUEST;
    }
}
