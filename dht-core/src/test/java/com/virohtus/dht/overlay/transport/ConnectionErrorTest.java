package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.event.EventFactory;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.event.UnsupportedEventException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ConnectionErrorTest {

    private final EventFactory eventFactory = EventFactory.getInstance();

    @Test
    public void testEventType() {
        ConnectionError connectionError = new ConnectionError(new Exception("bogus"));
        Assert.assertEquals(EventProtocol.CONNECTION_ERROR, connectionError.getType());
    }

    @Test
    public void testConnectionErrorSerialization() throws IOException, UnsupportedEventException {
        ConnectionError connectionError = new ConnectionError(new Exception("bogus"));
        byte[] data = connectionError.getData();
        ConnectionError reserialized = (ConnectionError) eventFactory.createEvent(data);
        Assert.assertEquals(connectionError, reserialized);
    }
}
