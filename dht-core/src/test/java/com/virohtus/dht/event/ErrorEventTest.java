package com.virohtus.dht.event;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ErrorEventTest {

    private final EventFactory eventFactory = EventFactory.getInstance();

    @Test
    public void testEventType() {
        ErrorEvent errorEvent = new ErrorEvent(new Exception("bogus"));
        Assert.assertEquals(EventProtocol.ERROR_EVENT, errorEvent.getType());
    }

    @Test
    public void testErrorEventSerialization() throws IOException, UnsupportedEventException {
        ErrorEvent errorEvent = new ErrorEvent(new Exception("bogus"));
        byte[] data = errorEvent.getData();
        ErrorEvent reserialized = (ErrorEvent) eventFactory.createEvent(data);
        Assert.assertEquals(errorEvent, reserialized);
    }
}
