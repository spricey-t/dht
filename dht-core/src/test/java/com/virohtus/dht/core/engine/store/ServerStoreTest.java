package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.ServerShutdown;
import com.virohtus.dht.core.engine.action.ServerStarted;
import com.virohtus.dht.core.peer.PeerType;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.server.ServerDelegate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerStoreTest {

    private static ExecutorService executorService = Executors.newCachedThreadPool();
    @Mock private ServerDelegate serverDelegate;
    @Mock private Dispatcher dispatcher;
    @Mock private PeerStore peerStore;
    @Mock private Store handler;
    @Mock private Connection connection;
    private ServerStore store;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        store = new ServerStore(dispatcher, executorService, peerStore, new InetSocketAddress(0));
        dispatcher.registerStore(handler);
    }

    @AfterClass
    public static void afterClass() {
        executorService.shutdownNow();
    }

    @Test
    public void testStart() throws InterruptedException {
        Assert.assertFalse(store.isAlive());
        store.start();
        Thread.sleep(200);
        Assert.assertTrue(store.isAlive());
        store.shutdown();
        Assert.assertFalse(store.isAlive());

        ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        Mockito.verify(dispatcher, Mockito.times(2)).dispatch(actionCaptor.capture());
        Assert.assertTrue(actionCaptor.getAllValues().get(0) instanceof ServerStarted);
        Assert.assertTrue(actionCaptor.getAllValues().get(1) instanceof ServerShutdown);
    }

    @Test
    public void testPeerAdded() {
        store.start();
        store.connectionOpened(connection);
        ArgumentCaptor<PeerType> peerTypeCaptor = ArgumentCaptor.forClass(PeerType.class);
        ArgumentCaptor<Connection> connectionCaptor = ArgumentCaptor.forClass(Connection.class);
        Mockito.verify(peerStore, Mockito.times(1)).createPeer(connectionCaptor.capture(), peerTypeCaptor.capture());
        Assert.assertEquals(PeerType.INCOMING, peerTypeCaptor.getValue());
        Assert.assertEquals(connection, connectionCaptor.getValue());
    }
}
