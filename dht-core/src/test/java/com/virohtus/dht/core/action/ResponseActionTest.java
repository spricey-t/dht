package com.virohtus.dht.core.action;

import com.virohtus.dht.core.transport.protocol.DhtEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ResponseActionTest {

    private static final String REQUEST_ID = "1234";
    private ResponseAction responseAction;

    @Before
    public void setup() {
        responseAction = new ResponseAction(REQUEST_ID) {
            @Override
            public int getType() {
                return -1;
            }
        };
    }

    @Test
    public void testGetRequestId() {
        Assert.assertEquals(REQUEST_ID, responseAction.getRequestId());
    }

    @Test
    public void testSerializeDeserialize() throws IOException {
        DhtEvent event = new DhtEvent(responseAction.serialize());
        ResponseAction reserialized = new ResponseAction(event) {
            @Override
            public int getType() {
                return -1;
            }
        };
        Assert.assertEquals(responseAction, reserialized);
    }
}
