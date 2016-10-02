package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.server.AsyncServer;
import com.virohtus.dht.core.transport.server.Server;
import com.virohtus.dht.core.transport.server.ServerDelegate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncConnectionTest {

    @Mock private ServerDelegate serverDelegate;
    @Mock private AsynchronousSocketChannel socketChannel;
    @Mock private ConnectionDelegate connectionDelegate;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private AtomicInteger invocationCount;
    private DhtEvent expectedEvent;

    private DhtEvent getTestEvent() {
        byte[] payload = {0, 1, 1};
        return new DhtEvent(payload);
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        byte[] payload = {0, 1, 2, 4};
        expectedEvent = new DhtEvent(payload);

        invocationCount = new AtomicInteger(0);
        Mockito.when(socketChannel.read(Mockito.any(ByteBuffer.class))).thenAnswer((invocationOnMock -> {
            ByteBuffer buffer = invocationOnMock.getArgumentAt(0, ByteBuffer.class);
            switch (invocationCount.get()) {
                case 0:
                    buffer.put(expectedEvent.getHeaders().getBytes());
                    break;
                case 1:
                    buffer.put(expectedEvent.getPayload());
                    break;
                default:
                    Thread.sleep(2000);
            }
            invocationCount.incrementAndGet();
            return CompletableFuture.completedFuture(buffer.position());
        }));
        Mockito.doThrow(new IOException()).when(socketChannel).close();
    }

    @Test
    public void testStartStop() throws InterruptedException, IOException {
        Server server = new AsyncServer(serverDelegate, executorService, new InetSocketAddress("localhost", 0));
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(server.getSocketAddress());

        Connection connection = new AsyncConnection(executorService, socketChannel);
        Assert.assertFalse(connection.isListening());
        connection.listen();
        Assert.assertTrue(connection.isListening());
        connection.close();
        Thread.sleep(200); // close does not block
        Assert.assertFalse(connection.isListening());

        server.shutdown();
    }

    @Test
    public void testDataReceived() throws IOException, InterruptedException {
        Connection connection = new AsyncConnection(executorService, socketChannel);
        connection.setConnectionDelegate(connectionDelegate);
        connection.listen();

        Thread.sleep(200);
        ArgumentCaptor<DhtEvent> eventCaptor = ArgumentCaptor.forClass(DhtEvent.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).dataReceived(eventCaptor.capture());
        Assert.assertEquals(expectedEvent, eventCaptor.getValue());
    }

    @Test
    public void testDataReceivedNoPayload() throws IOException, InterruptedException {
        expectedEvent = new DhtEvent(new byte[0]);
        Connection connection = new AsyncConnection(executorService, socketChannel);
        connection.setConnectionDelegate(connectionDelegate);
        connection.listen();

        Thread.sleep(200);
        ArgumentCaptor<DhtEvent> eventCaptor = ArgumentCaptor.forClass(DhtEvent.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).dataReceived(eventCaptor.capture());
        Assert.assertEquals(expectedEvent, eventCaptor.getValue());
    }
}
