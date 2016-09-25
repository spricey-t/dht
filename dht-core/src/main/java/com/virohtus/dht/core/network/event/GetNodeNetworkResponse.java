package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;

public class GetNodeNetworkResponse extends Event {

    private NodeNetwork nodeNetwork;

    public GetNodeNetworkResponse(NodeNetwork nodeNetwork) {
        this.nodeNetwork = nodeNetwork;
    }

    public GetNodeNetworkResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_NODE_NETWORK_RESPONSE;
    }

    public NodeNetwork getNodeNetwork() {
        return nodeNetwork;
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeSizedData(nodeNetwork.getBytes());
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        nodeNetwork = new NodeNetwork(dhtInputStream.readSizedData());
    }
}
