package com.virohtus.dht.transport.server;

import com.virohtus.dht.transport.connection.ConnectionDelegate;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncServerTest {

    @Mock private ServerDelegate serverDelegate;
    @Mock private ConnectionDelegate connectionDelegate;
    private Server server;
    private static ExecutorService executorService;

    @BeforeClass
    public static void setupClass() {
        executorService = Executors.newCachedThreadPool();
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        server = new AsyncServer(serverDelegate, connectionDelegate, executorService, 0);
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
}
