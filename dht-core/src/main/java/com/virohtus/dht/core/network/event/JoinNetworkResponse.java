package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.ResponseEvent;
import com.virohtus.dht.core.network.NodeLocalNet;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class JoinNetworkResponse extends ResponseEvent {

    private NodeLocalNet successorNet;

    public JoinNetworkResponse(String requestId, NodeLocalNet successorNet) {
        super(requestId);
        this.successorNet = successorNet;
    }

    public JoinNetworkResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.JOIN_NETWORK_RESPONSE;
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeEventSerializable(successorNet);
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        successorNet = dhtInputStream.readEventSerializable(NodeLocalNet.class);
    }

    public NodeLocalNet getSuccessorNet() {
        return successorNet;
    }
}
