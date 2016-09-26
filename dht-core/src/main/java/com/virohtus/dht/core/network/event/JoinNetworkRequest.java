package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.RequestEvent;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class JoinNetworkRequest extends RequestEvent {

    private NodeIdentity requestingIdentity;

    public JoinNetworkRequest(NodeIdentity requestingIdentity) {
        super();
        this.requestingIdentity = requestingIdentity;
    }

    public JoinNetworkRequest(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.JOIN_NETWORK_REQUEST;
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeSizedData(requestingIdentity.getBytes());
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        requestingIdentity = new NodeIdentity(dhtInputStream.readSizedData());
    }

    public NodeIdentity getRequestingIdentity() {
        return requestingIdentity;
    }
}
