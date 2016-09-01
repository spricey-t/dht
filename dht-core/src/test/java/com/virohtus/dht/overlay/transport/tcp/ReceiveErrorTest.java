package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.event.EventProtocol;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ReceiveErrorTest {

    @Test
    public void testEventType() {
        ReceiveError receiveError = new ReceiveError(new Exception("bogus"));
        Assert.assertEquals(EventProtocol.RECEIVER_ERROR, receiveError.getType());
    }

    @Test
    public void testReceiveErrorSerialization() throws IOException {
        ReceiveError receiveError = new ReceiveError(new Exception("bogus"));
        byte[] data = receiveError.getData();
        ReceiveError reserialized = new ReceiveError(data);
        Assert.assertEquals(receiveError, reserialized);
    }

}
