package com.virohtus.dht.core.transport.io;

import org.junit.Assert;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DhtInputStreamTest {

    @Test(timeout = 5000)
    public void testWriteSizedData() throws IOException {
        PipedInputStream pipedInputStream = new PipedInputStream();
        PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
        DataOutputStream dataOutputStream = new DataOutputStream(pipedOutputStream);

        byte[] data = {1, 2, 1, 4, 5};
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);

        DhtInputStream inputStream = new DhtInputStream(pipedInputStream);
        byte[] received = inputStream.readSizedData();
        Assert.assertArrayEquals(data, received);
    }
}
