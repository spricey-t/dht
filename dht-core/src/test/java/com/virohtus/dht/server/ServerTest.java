package com.virohtus.dht.server;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerTest {

    private static final int TEST_TIMEOUT = 5000;

    @Mock private ServerDelegate serverDelegate;
    private ExecutorService executorService;
    private Server server;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    @After
    public void cleanup() {
        executorService.shutdownNow();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testServerGetPort() throws IOException {
        Server server = new Server(serverDelegate, executorService, 0);
        Assert.assertEquals(-1, server.getPort());
        server.start();
        Assert.assertEquals(0, server.getRequestedPort());
        Assert.assertNotEquals(-1, server.getPort());
        server.shutdown();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testServerStart() throws IOException {
        Server server = new Server(serverDelegate, executorService, 0);
        server.start();
        Assert.assertTrue(server.isAlive());
        server.shutdown();
        Assert.assertFalse(server.isAlive());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testServerListens() throws IOException {
        Server server = new Server(serverDelegate, executorService, 0);
        server.start();
        Socket socket = new Socket("localhost", server.getPort());
        ArgumentCaptor<Socket> socketCaptor = ArgumentCaptor.forClass(Socket.class);
        Mockito.verify(serverDelegate, Mockito.times(1)).onClientConnect(socketCaptor.capture());
        Socket received = socketCaptor.getValue();
        Assert.assertEquals(socket.getPort(), received.getLocalPort());
        Assert.assertEquals(socket.getLocalPort(), received.getPort());
        server.shutdown();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testServerStartIdempotent() throws IOException {
        Server server = new Server(serverDelegate, executorService, 0);
        server.start();
        Assert.assertTrue(server.isAlive());
        server.start();
        Assert.assertTrue(server.isAlive());
        server.shutdown();
        Assert.assertFalse(server.isAlive());
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testServerShutdownIdempotent() throws IOException {
        Server server = new Server(serverDelegate, executorService, 0);
        Assert.assertFalse(server.isAlive());
        server.start();
        Assert.assertTrue(server.isAlive());
        server.shutdown();
        Assert.assertFalse(server.isAlive());
        server.shutdown();
        Assert.assertFalse(server.isAlive());
    }
}
