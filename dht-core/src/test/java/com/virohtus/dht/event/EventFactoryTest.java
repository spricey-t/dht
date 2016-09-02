package com.virohtus.dht.event;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EventFactoryTest {

    private final EventFactory eventFactory = EventFactory.getInstance();

    @Test(expected = UnsupportedEventException.class)
    public void testUnsupportedEventType() throws IOException, UnsupportedEventException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(-1);
        byte[] data = byteArrayOutputStream.toByteArray();

        eventFactory.createEvent(data);
    }
}
