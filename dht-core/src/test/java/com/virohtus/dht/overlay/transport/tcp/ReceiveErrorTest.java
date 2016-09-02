package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.event.EventFactory;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.event.UnsupportedEventException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ReceiveErrorTest {

    private final EventFactory eventFactory = EventFactory.getInstance();

    @Test
    public void testEventType() {
        ReceiveError receiveError = new ReceiveError(new Exception("bogus"));
        Assert.assertEquals(EventProtocol.RECEIVER_ERROR, receiveError.getType());
    }

    @Test
    public void testReceiveErrorSerialization() throws IOException, UnsupportedEventException {
        ReceiveError receiveError = new ReceiveError(new Exception("bogus"));
        byte[] data = receiveError.getData();
        ReceiveError reserialized = (ReceiveError) eventFactory.createEvent(data);
        Assert.assertEquals(receiveError, reserialized);
    }

}
