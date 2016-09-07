package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ServerDelegate;
import com.virohtus.dht.overlay.transport.tcp.TCPServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;

import java.io.IOException;
import java.net.Socket;

public class ServerTest {

    private static final long TEST_TIMEOUT = 5000;
    @Mock private ServerDelegate serverDelegate;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        mockServerDelegate();
    }

    private void mockServerDelegate() {
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testStartAndShutdown() throws InterruptedException {
        Server server = new TCPServer(serverDelegate, 0);
        server.start();
        Assert.assertTrue(server.serverRunning());
        server.shutdown();
        server.join();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testShutdownIdempotent() throws InterruptedException {
        Server server = new TCPServer(serverDelegate, 0);
        server.start();
        server.shutdown();
        server.join();
        server.shutdown();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testStartIdempotent() throws InterruptedException {
        Server server = new TCPServer(serverDelegate, 0);
        server.start();
        server.start();
        Assert.assertTrue(server.serverRunning());
        server.shutdown();
        server.join();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testGetPort() throws InterruptedException {
        int port = 11111;
        Server server = new Server(serverDelegate, port) {
            @Override
            protected void listen() {
                notifyStartupComplete();
                try {
                    Thread.sleep(TEST_TIMEOUT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public byte[] getAddress() {
                return new byte[0];
            }
        };

        server.start();
        Assert.assertEquals(port, server.getPort());
        server.shutdown();
        server.join();
    }

    @Test(timeout = TEST_TIMEOUT)
    public void testDelegateInvokedOnConnect() throws InterruptedException, IOException {
        Server server = new TCPServer(serverDelegate, 0);
        server.start();
        Socket socket = new Socket("localhost", server.getPort());
        Thread.sleep(200);

        final ArgumentCaptor<Socket> socketCaptor = ArgumentCaptor.forClass(Socket.class);
        Mockito.verify(serverDelegate, Mockito.times(1)).onClientConnect(socketCaptor.capture());

        Socket serverSideSocket = socketCaptor.getValue();
        Assert.assertEquals(socket.getLocalPort(), serverSideSocket.getPort());
        Assert.assertEquals(socket.getPort(), serverSideSocket.getLocalPort());

        socket.close();
        server.shutdown();
        server.join();
    }
}
