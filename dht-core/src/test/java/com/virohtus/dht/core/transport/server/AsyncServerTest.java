package com.virohtus.dht.core.transport.server;

import com.virohtus.dht.core.transport.connection.Connection;
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
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncServerTest {

    private static final int TEST_SERVER_PORT = 56523;
    @Mock private ServerDelegate serverDelegate;
    private ExecutorService executorService;
    private SocketAddress socketAddress;
    private Server asyncServer;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);

        executorService = Executors.newCachedThreadPool();
        socketAddress = new InetSocketAddress("localhost", 0);
        asyncServer = new AsyncServer(serverDelegate, executorService, socketAddress);
    }

    @After
    public void cleanup() {
        executorService.shutdownNow();
    }

    @Test
    public void testServerStartStop() {
        Assert.assertFalse(asyncServer.isListening());
        asyncServer.listen();
        Assert.assertTrue(asyncServer.isListening());
        asyncServer.shutdown();
        Assert.assertFalse(asyncServer.isListening());
        Mockito.verify(serverDelegate, Mockito.times(1)).serverShutdown(); //there is a chance server hasn't invoked yet
        Mockito.verify(serverDelegate, Mockito.times(0)).connectionOpened(ArgumentCaptor.forClass(Connection.class).capture());
    }

    @Test
    public void testServerAcceptsConnection() throws IOException, InterruptedException {
        asyncServer.listen();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(asyncServer.getSocketAddress());
        Thread.sleep(200); // give some processing time
        ArgumentCaptor<Connection> connectionCaptor = ArgumentCaptor.forClass(Connection.class);
        Mockito.verify(serverDelegate, Mockito.times(1)).connectionOpened(connectionCaptor.capture());
        asyncServer.shutdown();
        socketChannel.close();
    }

    @Test
    public void testServerAcceptsMultipleConnections() throws IOException, InterruptedException {
        asyncServer.listen();
        Set<SocketChannel> sockets = new HashSet<>();
        int numInvokations = 3;
        for(int i = 0; i < numInvokations; i++) {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(asyncServer.getSocketAddress());
        }
        Thread.sleep(400); // give some processing time
        ArgumentCaptor<Connection> connectionCaptor = ArgumentCaptor.forClass(Connection.class);
        Mockito.verify(serverDelegate, Mockito.times(numInvokations)).connectionOpened(connectionCaptor.capture());
        asyncServer.shutdown();
        for(SocketChannel socketChannel : sockets) {
            socketChannel.close();
        }
    }

    @Test
    public void testGetSocketAddress() throws IOException {
        SocketAddress predetermined = new InetSocketAddress("localhost", TEST_SERVER_PORT);
        asyncServer = new AsyncServer(serverDelegate, executorService, predetermined);
        Assert.assertEquals(predetermined, asyncServer.getSocketAddress());
        asyncServer.listen();
        Assert.assertEquals(predetermined, asyncServer.getSocketAddress());
        asyncServer.shutdown();
    }
}
