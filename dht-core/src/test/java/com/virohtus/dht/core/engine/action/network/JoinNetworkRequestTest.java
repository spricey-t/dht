package com.virohtus.dht.core.engine.action.network;

import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.junit.Assert;
import org.junit.Test;

public class JoinNetworkRequestTest {

    @Test
    public void testGetType() {
        JoinNetworkRequest request = new JoinNetworkRequest();
        Assert.assertEquals(DhtProtocol.JOIN_NETWORK_REQUEST, request.getType());
    }
}
