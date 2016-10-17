package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.network.FingerTable;
import com.virohtus.dht.core.network.Keyspace;
import com.virohtus.dht.core.network.Node;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.junit.Assert;
import org.junit.Test;

public class JoinNetworkRequestTest {

    @Test
    public void testGetType() {
        JoinNetworkRequest request = new JoinNetworkRequest(new Node(new NodeIdentity("123", null), new Keyspace(), new FingerTable()));
        Assert.assertEquals(DhtProtocol.JOIN_NETWORK_REQUEST, request.getType());
    }
}
