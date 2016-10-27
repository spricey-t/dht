package com.virohtus.dht.core.engine.action.peer;

import com.virohtus.dht.core.network.peer.Peer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PeerDisconnectedTest {

    @Mock private Peer peer;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPeer() {
        PeerDisconnected peerDisconnected = new PeerDisconnected(peer);
        Assert.assertEquals(peer, peerDisconnected.getPeer());
    }
}
