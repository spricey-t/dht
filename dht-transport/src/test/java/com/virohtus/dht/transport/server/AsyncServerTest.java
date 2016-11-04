package com.virohtus.dht.transport.server;

import com.virohtus.dht.transport.connection.ConnectionDelegate;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncServerTest {

    @Mock private ServerDelegate serverDelegate;
    @Mock private ConnectionDelegate connectionDelegate;
    private AccessPoint accessPoint;
    private Server server;
    private static ExecutorService executorService;

    @BeforeClass
    public static void setupClass() {
        executorService = Executors.newCachedThreadPool();
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        accessPoint = new AccessPoint("localhost", 0);
        server = new AsyncServer(serverDelegate, connectionDelegate, executorService, accessPoint);
    }

    @After
    public void teardown() {
        server.shutdown();
    }

    @AfterClass
    public static void teardownClass() {
        executorService.shutdownNow();
    }

    @Test
    public void testStartStop() {
        Assert.assertFalse(server.isListening());
        server.listen();
        Assert.assertTrue(server.isListening());
        server.shutdown();
        Assert.assertFalse(server.isListening());
    }

    @Test
    public void testGetAccessPoint() {
        Assert.assertEquals(accessPoint, server.getAccessPoint());
    }

    @Test
    public void testConnectionOpened() throws IOException, InterruptedException {
        server.listen();
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(new InetSocketAddress(accessPoint.getHost(), accessPoint.getPort()));

        Thread.sleep(200); // give time for server to notify delegate

        Mockito.verify(serverDelegate, Mockito.times(1)).connectionOpened(Mockito.any());
        socketChannel.close();
    }
}
