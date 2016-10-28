package com.virohtus.dht.core.engine.action.server;

import com.virohtus.dht.core.transport.server.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServerStartedTest {

    @Mock
    private Server server;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetServer() {
        ServerStarted serverStarted = new ServerStarted(server);
        Assert.assertEquals(server, serverStarted.getServer());
    }
}
