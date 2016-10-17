package com.virohtus.dht.core.transport.protocol;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DhtEventTest {

    private Headers headers;
    private byte[] payload = {1, 4, 2, 1, 3, 5, 5};

    @Before
    public void setup() {
        headers = new Headers(1, payload.length);
    }

    private byte[] getExpectedData(Headers headers, byte[] payload) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.write(headers.getBytes());
        dataOutputStream.write(payload);
        dataOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testCreate() {
        DhtEvent event = new DhtEvent(headers, payload);
        Assert.assertEquals(headers, event.getHeaders());
        Assert.assertArrayEquals(payload, event.getPayload());
    }

    @Test
    public void testCreateNoPayload() {
        Headers headers = new Headers(1, 0);
        byte[] payload = new byte[0];
        DhtEvent event = new DhtEvent(headers, payload);
        Assert.assertEquals(headers, event.getHeaders());
        Assert.assertArrayEquals(payload, event.getPayload());
    }

    @Test
    public void testSerialize() throws IOException {
        DhtEvent event = new DhtEvent(headers, payload);
        byte[] data = event.getBytes();
        Assert.assertArrayEquals(getExpectedData(headers, payload), data);
    }
}
