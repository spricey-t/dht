package com.virohtus.dht.event;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class StringMessageEventTest {

    private final EventFactory eventFactory = EventFactory.getInstance();

    @Test
    public void testEventType() throws IOException {
        StringMessageEvent stringMessageEvent = new StringMessageEvent("test msg");
        Assert.assertEquals(EventProtocol.STRING_MESSAGE_EVENT, stringMessageEvent.getType());
    }

    @Test
    public void testStringMessageEventSerialization() throws IOException, UnsupportedEventException {
        StringMessageEvent stringMessageEvent = new StringMessageEvent("test msg");
        byte[] data = stringMessageEvent.getData();
        StringMessageEvent reserialized = (StringMessageEvent) eventFactory.createEvent(data);
        Assert.assertEquals(stringMessageEvent, reserialized);
    }
}
