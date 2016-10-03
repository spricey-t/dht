package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.Dispatcher;
import com.virohtus.dht.core.engine.action.PeerDisconnected;
import com.virohtus.dht.core.engine.action.network.JoinNetworkRequest;
import com.virohtus.dht.core.transport.connection.Connection;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerTest {

    @Mock private Dispatcher dispatcher;
    @Mock private Connection connection;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Peer peer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        peer = new Peer(dispatcher, executorService, PeerType.INCOMING, connection);

        Mockito.when(connection.isListening()).thenReturn(false);
    }

    @Test
    public void testGetId() {
        Assert.assertTrue(peer.getId() != null);
    }

    @Test
    public void testCreate() {
        ArgumentCaptor<Peer> peerCaptor = ArgumentCaptor.forClass(Peer.class);
        Mockito.verify(connection, Mockito.times(1)).setConnectionDelegate(peerCaptor.capture());
        Assert.assertEquals(peer, peerCaptor.getValue());
        Assert.assertEquals(peer.getType(), PeerType.INCOMING);
        Assert.assertEquals(peer.getConnection(), connection);
    }

    @Test
    public void testStartStop() throws InterruptedException {
        peer.listen();
        Mockito.verify(connection, Mockito.times(1)).listen();
        peer.shutdown();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void testSend() throws IOException {
        byte[] data = {1, 2, 1, 4, 1};
        DhtEvent event = new DhtEvent(data);
        peer.send(event);
        ArgumentCaptor<DhtEvent> eventCaptor = ArgumentCaptor.forClass(DhtEvent.class);
        Mockito.verify(connection, Mockito.times(1)).send(eventCaptor.capture());
        Assert.assertEquals(event, eventCaptor.getValue());
    }

    @Test
    public void testActionDispatchedOnReceive() throws IOException {
        JoinNetworkRequest request = new JoinNetworkRequest();
        DhtEvent event = new DhtEvent(request.serialize());
        peer.dataReceived(event);
        ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        Mockito.verify(dispatcher, Mockito.times(1)).dispatch(actionCaptor.capture());
        Assert.assertEquals(request, actionCaptor.getValue());
    }

    @Test
    public void testPeerDisconnectedActionDispatched() {
        peer.listen();
        Mockito.doAnswer((invocationOnMock -> {
            peer.listenerDisrupted();
            return invocationOnMock;
        })).when(connection).close();
        peer.shutdown();

        ArgumentCaptor<PeerDisconnected> peerDisconnectedCaptor = ArgumentCaptor.forClass(PeerDisconnected.class);
        Mockito.verify(dispatcher, Mockito.times(1)).dispatch(peerDisconnectedCaptor.capture());
    }
}
