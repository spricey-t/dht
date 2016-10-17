package com.virohtus.dht.core.transport.io;

import org.junit.Assert;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DhtOutputStreamTest {

    @Test(timeout = 5000)
    public void testWriteSizedData() throws IOException {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        DataInputStream dataInputStream = new DataInputStream(pipedInputStream);

        byte[] data = {1, 2, 4, 1, 2, 1};
        DhtOutputStream outputStream = new DhtOutputStream(pipedOutputStream);
        outputStream.writeSizedData(data);

        int receivedLength = dataInputStream.readInt();
        byte[] received = new byte[receivedLength];
        dataInputStream.readFully(received);
        Assert.assertArrayEquals(data, received);
    }
}
