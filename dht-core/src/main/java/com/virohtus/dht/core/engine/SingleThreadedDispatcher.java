package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleThreadedDispatcher implements Dispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(SingleThreadedDispatcher.class);
    private final ExecutorService executorService;
    private final List<Store> stores;
    private final BlockingQueue<Action> actionQueue;
    private final AtomicBoolean shutdownRequested;
    private Future worker;

    public SingleThreadedDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
        stores = new ArrayList<>();
        actionQueue = new LinkedBlockingQueue<>();
        shutdownRequested = new AtomicBoolean(false);
    }

    @Override
    public void dispatch(Action action) {
        try {
            synchronized (shutdownRequested) {
                if(!shutdownRequested.get()) {
                    actionQueue.put(action);
                }
            }
        } catch (InterruptedException e) {
            LOG.warn("interrupted when adding action to dispatch queue!");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start() {
        if(isAlive()) {
            return;
        }
        synchronized (shutdownRequested) {
            shutdownRequested.set(false);
        }
        worker = executorService.submit(() -> {
            LOG.info("dispatcher started");
            while(!Thread.currentThread().isInterrupted()) {
                synchronized (shutdownRequested) {
                    if(shutdownRequested.get() && actionQueue.isEmpty()) {
                        break;
                    }
                }
                try {
                    Action action = actionQueue.take();
//                    LOG.info("dispatching: " + action.getClass().getName());
                    listStores().forEach(store -> store.onAction(action));
                } catch (InterruptedException e) {
                    LOG.warn("interrupted when taking action from dispatch queue!");
                    Thread.currentThread().interrupt();
                }
            }
            LOG.info("dispatcher stopped");
        });
    }

    @Override
    public void shutdown() {
        if(!isAlive()) {
            return;
        }
        synchronized (shutdownRequested) {
            shutdownRequested.set(true);
            if(actionQueue.isEmpty()) {
                worker.cancel(true);
            }
        }
    }

    @Override
    public boolean isAlive() {
        return worker != null && !worker.isCancelled() && !worker.isDone();
    }

    @Override
    public void registerStore(Store store) {
        synchronized (stores) {
            stores.add(store);
        }
    }

    @Override
    public void unregisterStore(Store store) {
        synchronized (stores) {
            stores.remove(store);
        }
    }

    @Override
    public List<Store> listStores() {
        synchronized (stores) {
            return new ArrayList<>(stores);
        }
    }
}
