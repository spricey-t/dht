package com.virohtus.dht.core.action;

import com.virohtus.dht.core.network.peer.Peer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RequestActionTest {

    @Mock private Peer peer;
    private RequestAction requestAction;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        requestAction = new RequestAction() {
            @Override
            public int getType() {
                return -1;
            }
        };
        requestAction.setSourcePeer(peer);
    }

    @Test
    public void testRequestId() {
        Assert.assertFalse(requestAction.getRequestId() == null);
        RequestAction other = new RequestAction() {
            @Override
            public int getType() {
                return -2;
            }
        };
        Assert.assertNotEquals(other.getRequestId(), requestAction.getRequestId());
    }

    @Test
    public void testType() {
        Assert.assertEquals(-1, requestAction.getType());
    }

    @Test
    public void testSourcePeer() {
        Assert.assertEquals(peer, requestAction.getSourcePeer());
    }
}
