package com.virohtus.dht.event;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EventTest {

    @Test
    public void testHashcode() {
        Event first = createTestEvent();
        Event second = createTestEvent();
        System.out.println(first.hashCode());
        Assert.assertEquals(first.hashCode(), second.hashCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalEventType() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();

        byte[] data = byteArrayOutputStream.toByteArray();
        new Event(data) {
            @Override
            public int getType() {
                return -1;
            }
        };
    }

    private Event createTestEvent() {
        return new Event() {
            @Override
            public int getType() {
                return -1;
            }
        };
    }
}
