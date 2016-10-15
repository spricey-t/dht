package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.action.network.JoinNetworkRequest;
import com.virohtus.dht.core.engine.store.Store;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadedDispatcherTest {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SingleThreadedDispatcher singleThreadedDispatcher;
    @Mock private Store store;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        singleThreadedDispatcher = new SingleThreadedDispatcher(executorService);
        singleThreadedDispatcher.registerStore(store);
    }

    @Test
    public void testStartStop() {
        Assert.assertFalse(singleThreadedDispatcher.isAlive());
        singleThreadedDispatcher.start();
        Assert.assertTrue(singleThreadedDispatcher.isAlive());
        singleThreadedDispatcher.shutdown();
        Assert.assertFalse(singleThreadedDispatcher.isAlive());
    }

    @Test
    public void testDispatch() throws InterruptedException {
        Action action = Mockito.mock(Action.class);
        singleThreadedDispatcher.start();
        singleThreadedDispatcher.dispatch(action);
        Thread.sleep(200);
        ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        Mockito.verify(store, Mockito.times(1)).onAction(actionCaptor.capture());
        Assert.assertEquals(action, actionCaptor.getValue());
        singleThreadedDispatcher.shutdown();
    }

    @Test
    public void testDispatchNewStoreAdded() throws InterruptedException {
        Action action = Mockito.mock(Action.class);
        singleThreadedDispatcher.start();
        Store newStore = Mockito.mock(Store.class);
        singleThreadedDispatcher.registerStore(newStore);
        singleThreadedDispatcher.dispatch(action);
        Thread.sleep(200);
        ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        Mockito.verify(newStore, Mockito.times(1)).onAction(actionCaptor.capture());
        Assert.assertEquals(action, actionCaptor.getValue());
        singleThreadedDispatcher.shutdown();
    }

    @Test
    public void testUnregisterStore() throws InterruptedException {
        Action action = Mockito.mock(Action.class);
        singleThreadedDispatcher.start();
        Store newStore = Mockito.mock(Store.class);
        singleThreadedDispatcher.registerStore(newStore);
        singleThreadedDispatcher.unregisterStore(newStore);
        singleThreadedDispatcher.dispatch(action);
        Thread.sleep(200);
        ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        Mockito.verify(newStore, Mockito.times(0)).onAction(actionCaptor.capture());
        singleThreadedDispatcher.shutdown();
    }

    @Test
    public void testListStores() {
        Assert.assertEquals(Arrays.asList(store), singleThreadedDispatcher.listStores());
    }
}
