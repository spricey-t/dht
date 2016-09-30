package com.virohtus.dht.transport;

import org.junit.Assert;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DhtOutputStreamTest {

    @Test
    public void testWriteSizedData() throws IOException {
        byte[] expectedData = {0, 1, 4, 2, 3};

        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        DataInputStream dataInputStream = new DataInputStream(pipedInputStream);

        DhtOutputStream dhtOutputStream = new DhtOutputStream(pipedOutputStream);
        dhtOutputStream.writeSizedData(expectedData);

        int receivedLength = dataInputStream.readInt();
        byte[] received = new byte[receivedLength];
        dataInputStream.readFully(received);
        Assert.assertArrayEquals(expectedData, received);
    }
}
