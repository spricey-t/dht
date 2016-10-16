package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.action.TransportableAction;
import com.virohtus.dht.core.network.Network;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;
import java.util.Arrays;

public class GetNetwork extends TransportableAction {

    private Network network;

    public GetNetwork(Network network) {
        this.network = network;
    }

    public GetNetwork(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public int getType() {
        return DhtProtocol.GET_NETWORK;
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        super.toWire(outputStream);
        network.toWire(outputStream);
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        super.fromWire(inputStream);
        network = new Network(inputStream);
    }
}
