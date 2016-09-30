package com.virohtus.dht.transport;

import org.junit.Assert;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DhtInputStreamTest {

    @Test
    public void testReadSizedData() throws IOException {
        byte[] expectedData = {1, 2, 3, 5, 1, 2};

        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(pipedOutputStream);
        dataOutputStream.writeInt(expectedData.length);
        dataOutputStream.write(expectedData);

        DhtInputStream dhtInputStream = new DhtInputStream(pipedInputStream);
        byte[] data = dhtInputStream.readSizedData();
        Assert.assertArrayEquals(expectedData, data);
    }
}
