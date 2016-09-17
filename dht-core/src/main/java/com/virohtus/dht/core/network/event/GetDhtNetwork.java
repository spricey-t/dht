package com.virohtus.dht.core.network.event;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetDhtNetwork extends Event {

    private List<NodeNetwork> nodeNets;

    public GetDhtNetwork() {
        nodeNets = new ArrayList<>();
    }

    public GetDhtNetwork(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_DHT_NETWORK;
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeInt(nodeNets.size());
        for(NodeNetwork nodeNet : nodeNets) {
            dhtOutputStream.writeSizedData(nodeNet.getBytes());
        }
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        int nodeNetSize = dhtInputStream.readInt();
        for(int i = 0; i < nodeNetSize; i++) {
            nodeNets.add(new NodeNetwork(dhtInputStream.readSizedData()));
        }
    }

    public void addNodeNet(NodeNetwork nodeNetwork) {
        nodeNets.add(nodeNetwork);
    }

    public List<NodeNetwork> getNodeNets() {
        return new ArrayList<>(nodeNets);
    }
}
