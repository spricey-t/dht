package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.transport.protocol.Headers;
import com.virohtus.dht.core.transport.server.AsyncServer;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.ServerDelegate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncConnectionTest {

    @Mock private ConnectionDelegate connectionDelegate;
    @Mock private ServerDelegate serverDelegate;
    private ExecutorService executorService;
    private Server server;
    private Connection connection;
    private Connection serverConnection;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        executorService = Executors.newCachedThreadPool();
        server = new AsyncServer(serverDelegate, executorService, new InetSocketAddress("localhost", 0));
        server.listen();
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(server.getSocketAddress());
        connection = new AsyncConnection(executorService, socketChannel);
        connection.setConnectionDelegate(connectionDelegate);
    }

    @After
    public void tearDown() {
        connection.close();
        if(serverConnection != null) {
            serverConnection.close();
        }
        server.shutdown();
        executorService.shutdownNow();
    }

    @Test
    public void testStartStop() throws InterruptedException {
        Assert.assertFalse(connection.isListening());
        connection.listen();
        Assert.assertTrue(connection.isListening());
        connection.close();
        Thread.sleep(200); //give some processing time
        Assert.assertFalse(connection.isListening());
        Mockito.verify(connectionDelegate, Mockito.times(1)).listenerDisrupted();
    }

    @Test
    public void testDataReceived() throws IOException, InterruptedException {
        ArgumentCaptor<Connection> connectionCaptor = ArgumentCaptor.forClass(Connection.class);
        Mockito.verify(serverDelegate, Mockito.times(1)).connectionOpened(connectionCaptor.capture());
        serverConnection = connectionCaptor.getValue();

        connection.listen();
        byte[] data = {1, 2, 1, 4, 1};
        serverConnection.send(new DhtEvent(data));

        Thread.sleep(200); // give some processing time
        ArgumentCaptor<DhtEvent> eventCaptor = ArgumentCaptor.forClass(DhtEvent.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).dataReceived(eventCaptor.capture());
        Assert.assertArrayEquals(data, eventCaptor.getValue().getPayload());
    }

    @Test
    public void testDataReceivedNoPayload() throws IOException, InterruptedException {
        ArgumentCaptor<Connection> connectionCaptor = ArgumentCaptor.forClass(Connection.class);
        Mockito.verify(serverDelegate, Mockito.times(1)).connectionOpened(connectionCaptor.capture());
        serverConnection = connectionCaptor.getValue();

        connection.listen();
        byte[] data = new byte[0];
        serverConnection.send(new DhtEvent(data));

        Thread.sleep(200); // give some processing time
        ArgumentCaptor<DhtEvent> eventCaptor = ArgumentCaptor.forClass(DhtEvent.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).dataReceived(eventCaptor.capture());
        Assert.assertArrayEquals(data, eventCaptor.getValue().getPayload());
    }

    @Test
    public void testDataReceivedInMultipleParts() throws IOException, InterruptedException {
        ArgumentCaptor<Connection> connectionCaptor = ArgumentCaptor.forClass(Connection.class);
        Mockito.verify(serverDelegate, Mockito.times(1)).connectionOpened(connectionCaptor.capture());
        serverConnection = connectionCaptor.getValue();

        connection.listen();
        byte[] data = {1, 2, 1, 4, 1};
        byte[] expected = {1, 2, 1, 4, 1, 0, 0, 0, 1, 0, 0, 0, 1};
        DhtEvent partialEvent = new DhtEvent(new Headers(DhtProtocol.PROTOCOL_VERSION, expected.length), data);
        serverConnection.send(partialEvent);
        // this is a hack to finish off the partial event, the header will be 8 bytes, payload 0
        DhtEvent bogusEvent = new DhtEvent(new Headers(DhtProtocol.PROTOCOL_VERSION, 1), new byte[0]);
        serverConnection.send(bogusEvent);

        Thread.sleep(200); // give some processing time
        ArgumentCaptor<DhtEvent> eventCaptor = ArgumentCaptor.forClass(DhtEvent.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).dataReceived(eventCaptor.capture());
        Assert.assertArrayEquals(expected, eventCaptor.getValue().getPayload());
    }
}
