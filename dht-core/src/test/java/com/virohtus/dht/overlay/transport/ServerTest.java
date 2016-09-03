package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.node.ServerDelegate;
import com.virohtus.dht.overlay.transport.tcp.TCPServer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServerTest {

    @Mock private ServerDelegate serverDelegate;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        mockServerDelegate();
    }

    private void mockServerDelegate() {
    }

    @Test(timeout = 5000)
    public void testStartAndShutdown() throws InterruptedException {
        Server server = new TCPServer(serverDelegate, 0);
        server.start();
        server.shutdown();
        server.join();
    }
}
