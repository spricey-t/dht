package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.protocol.Message;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncConnectionTest {

    @Mock private ConnectionDelegate connectionDelegate;
    @Mock private AsynchronousSocketChannel asynchronousSocketChannel;
    private Connection connection;
    private static ExecutorService executorService;

    @BeforeClass
    public static void setupClass() {
        executorService = Executors.newCachedThreadPool();
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        connection = new AsyncConnection(connectionDelegate, executorService, asynchronousSocketChannel);
        mockAsynchronousSocketChannel();
    }

    @AfterClass
    public static void teardownClass() {
        executorService.shutdownNow();
    }

    private Message getTestMessage() {
        byte[] headers = new byte[0];
        byte[] payload = {1, 1, 1};
        return new Message(ByteBuffer.wrap(headers), ByteBuffer.wrap(payload));
    }

    private void mockAsynchronousSocketChannel() {
        byte[] data = getTestMessage().serialize();
        AtomicInteger written = new AtomicInteger(0);
        Mockito.when(asynchronousSocketChannel.read(Mockito.any(ByteBuffer.class))).thenAnswer((invocation) -> {
            if(written.get() == data.length) {
                Thread.sleep(2000);
                return CompletableFuture.completedFuture(0);
            }
            ByteBuffer byteBuffer = invocation.getArgumentAt(0, ByteBuffer.class);
            byteBuffer.put(data, written.get(), byteBuffer.capacity());
            written.set(written.get() + byteBuffer.capacity());
            return CompletableFuture.completedFuture(byteBuffer.capacity());
        });
        Mockito.when(asynchronousSocketChannel.write(Mockito.any(ByteBuffer.class))).thenAnswer((invocation) -> {
            ByteBuffer byteBuffer = invocation.getArgumentAt(0, ByteBuffer.class);
            byteBuffer.get(new byte[byteBuffer.capacity()]);
            return CompletableFuture.completedFuture(byteBuffer.capacity());
        });
    }


    @Test
    public void testStartStop() {
        Assert.assertFalse(connection.isListening());
        connection.listen();
        Assert.assertTrue(connection.isListening());
        connection.shutdown();
        Assert.assertFalse(connection.isListening());
    }

    @Test
    public void testMessageReceived() throws InterruptedException {
        connection.listen();

        Thread.sleep(200); // give some time to construct message

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).receive(messageCaptor.capture());
        Assert.assertEquals(getTestMessage(), messageCaptor.getValue());

        Mockito.verifyNoMoreInteractions(connectionDelegate);
    }

    @Test
    public void testSendMessage() throws ConnectionException, InterruptedException {
        connection.listen();
        connection.send(getTestMessage());

        Thread.sleep(200); // give some time to send message

        ArgumentCaptor<ByteBuffer> byteBufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(asynchronousSocketChannel, Mockito.times(1)).write(byteBufferCaptor.capture());
        Assert.assertArrayEquals(getTestMessage().serialize(), byteBufferCaptor.getValue().array());

        Mockito.verify(connectionDelegate, Mockito.times(0)).receiverError(Mockito.any());
    }

    @Test
    public void testReceiverError() throws InterruptedException {
        AsynchronousSocketChannel brokenChannel = Mockito.mock(AsynchronousSocketChannel.class);
        Mockito.when(brokenChannel.read(Mockito.any())).thenAnswer((invocation) -> {
            throw new ShutdownChannelGroupException();
        });
        Connection brokenConnection = new AsyncConnection(connectionDelegate, executorService, brokenChannel);
        brokenConnection.listen();

        Thread.sleep(200); // give some time to propogate error

        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).receiverError(exceptionCaptor.capture());
    }
}
