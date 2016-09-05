package com.virohtus.dht.route.event;

import com.virohtus.dht.event.EventFactory;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.event.UnsupportedEventException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class RequestFingerTableEventTest {

    private EventFactory eventFactory = EventFactory.getInstance();

    @Test
    public void testEventType() {
        RequestFingerTableEvent event = new RequestFingerTableEvent();
        Assert.assertEquals(EventProtocol.REQUEST_FINGER_TABLE_EVENT, event.getType());
    }

    @Test
    public void testEventSerialization() throws IOException, UnsupportedEventException {
        RequestFingerTableEvent event = new RequestFingerTableEvent();
        byte[] data = event.getData();
        RequestFingerTableEvent reserialized = (RequestFingerTableEvent) eventFactory.createEvent(data);
        Assert.assertEquals(event, reserialized);
    }
}
