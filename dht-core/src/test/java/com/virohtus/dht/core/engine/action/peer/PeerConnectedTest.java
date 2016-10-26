package com.virohtus.dht.core.engine.action.peer;

import com.virohtus.dht.core.network.peer.Peer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PeerConnectedTest {

    @Mock private Peer peer;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPeer() {
        PeerConnected peerConnected = new PeerConnected(peer);
        Assert.assertEquals(peer, peerConnected.getPeer());
    }
}
