package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.DhtInputStream;
import com.virohtus.dht.transport.DhtOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionTest {

    private Connection connection;
    @Mock private ConnectionDelegate connectionDelegate;
    private ExecutorService executorService;
    private PipedOutputStream pipedOutputStream;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        executorService = Executors.newFixedThreadPool(3);
        PipedInputStream pipedInputStream = new PipedInputStream();
        pipedOutputStream = new PipedOutputStream(pipedInputStream);
        connection = new Connection(connectionDelegate, executorService) {
            @Override
            public void send(byte[] data) throws IOException {
            }

            @Override
            protected byte[] receive() throws IOException {
                DhtInputStream inputStream = new DhtInputStream(pipedInputStream);
                return inputStream.readSizedData();
            }

            @Override
            protected void cleanup() {
            }
        };
    }

    @Test
    public void testListen() {
        Assert.assertFalse(connection.isListening());
        connection.listen();
        Assert.assertTrue(connection.isListening());
        connection.listen();
        Assert.assertTrue(connection.isListening());
        connection.close();
        Assert.assertFalse(connection.isListening());
    }

    @Test
    public void testClose() throws InterruptedException {
        connection.listen();
        connection.close();
        connection.close();

        Mockito.verify(connectionDelegate, Mockito.times(1)).connectionClosed();
        Assert.assertFalse(connection.isListening());
    }

    @Test
    public void testReceiveResultInvokesDelegate() throws IOException, InterruptedException {
        byte[] testData = {1, 2, 3, 4, 1};
        DhtOutputStream outputStream = new DhtOutputStream(pipedOutputStream);
        outputStream.writeSizedData(testData);
        outputStream.flush();

        connection.listen();

        Thread.sleep(200); // give some processing time
        ArgumentCaptor<byte[]> dataCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).dataReceived(dataCaptor.capture());
        Assert.assertArrayEquals(testData, dataCaptor.getValue());
    }
}
