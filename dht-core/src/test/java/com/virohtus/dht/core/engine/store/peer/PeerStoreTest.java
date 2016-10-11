package com.virohtus.dht.core.engine.store.peer;

import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.peer.PeerDisconnected;
import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.network.peer.PeerNotFoundException;
import com.virohtus.dht.core.network.peer.PeerType;
import com.virohtus.dht.core.transport.connection.Connection;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PeerStoreTest {

    @Mock private Dispatcher dispatcher;
    @Mock private Peer peer;
    @Mock private Connection connection;
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private PeerStore peerStore;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        peerStore = new PeerStore(dispatcher, executorService);

        Mockito.when(peer.getId()).thenReturn(getTestPeerId());
    }

    @AfterClass
    public static void classCleanup() {
        executorService.shutdownNow();
    }

    private String getTestPeerId() {
        return "testPeerId";
    }

    @Test
    public void testAddPeer() throws PeerNotFoundException {
        peerStore.addPeer(peer);
        Assert.assertEquals(peer, peerStore.getPeer(peer.getId()));
    }

    @Test(expected = PeerNotFoundException.class)
    public void testGetPeerNonexistent() throws PeerNotFoundException {
        peerStore.getPeer("bogus");
    }

    @Test(expected = PeerNotFoundException.class)
    public void testRemovePeer() throws PeerNotFoundException {
        peerStore.addPeer(peer);
        peerStore.removePeer(peer);
        peerStore.getPeer(getTestPeerId());
    }

    @Test
    public void listPeers() {
        peerStore.addPeer(peer);
        Assert.assertEquals(new HashSet<>(Arrays.asList(peer)), peerStore.listPeers());
    }

    @Test
    public void clearPeers() {
        peerStore.addPeer(peer);
        peerStore.clearPeers();
        Assert.assertEquals(0, peerStore.listPeers().size());
    }

    @Test
    public void testCreatePeerWithInet() throws IOException {
        peerStore.createPeer(new InetSocketAddress(0));
    }

    @Test
    public void testCreatePeerWithConnection() throws PeerNotFoundException {
        Peer peer = peerStore.createPeer(connection, PeerType.INCOMING);
        Assert.assertEquals(peer, peerStore.getPeer(peer.getId()));
    }

    @Test
    public void testShutdown() throws InterruptedException {
        peerStore.addPeer(peer);
        Executors.newScheduledThreadPool(1).schedule(() -> {
                    peerStore.onAction(new PeerDisconnected(peer));
        }, 200, TimeUnit.MILLISECONDS);
        peerStore.shutdown();
        Thread.sleep(200);
        Mockito.verify(peer, Mockito.times(1)).shutdown();
    }

    @Test
    public void testPeerRemovedOnDisconnect() {
        peerStore.addPeer(peer);
        peerStore.onAction(new PeerDisconnected(peer));
        Assert.assertEquals(0, peerStore.listPeers().size());
    }
}
