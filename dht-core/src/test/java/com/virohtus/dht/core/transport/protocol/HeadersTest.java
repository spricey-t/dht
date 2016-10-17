package com.virohtus.dht.core.transport.protocol;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HeadersTest {

    private int version = 0;
    private int payloadLength = 0;

    @Before
    public void setup() {
        version = 1;
        payloadLength = 4;
    }

    public byte[] getExpectedSerialized() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(version);
        dataOutputStream.writeInt(payloadLength);
        dataOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @Test
    public void testCreate() {
        Headers headers = new Headers(version, payloadLength);
        Assert.assertEquals(version, headers.getVersion());
        Assert.assertEquals(payloadLength, headers.getPayloadLength());
    }

    @Test
    public void testCreateWithNoPayload() {
        int payloadLength = 0;
        Headers headers = new Headers(version, payloadLength);
        Assert.assertEquals(version, headers.getVersion());
        Assert.assertEquals(payloadLength, headers.getPayloadLength());
    }

    @Test
    public void testSerialize() throws IOException {
        Headers headers = new Headers(version, payloadLength);
        byte[] serialized = headers.getBytes();
        Assert.assertArrayEquals(getExpectedSerialized(), serialized);
    }

    @Test
    public void testDeserialize() throws IOException {
        Headers headers = Headers.deserialize(getExpectedSerialized());
        Assert.assertEquals(version, headers.getVersion());
        Assert.assertEquals(payloadLength, headers.getPayloadLength());
    }

    @Test
    public void testBoth() throws IOException {
        Headers headers = new Headers(version, payloadLength);
        byte[] data = headers.getBytes();
        Headers reserialized = Headers.deserialize(data);
        Assert.assertEquals(headers, reserialized);
    }
}
