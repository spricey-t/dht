package com.virohtus.dht.core.engine.store.network;

import com.virohtus.dht.core.DhtNodeManager;
import com.virohtus.dht.core.action.Action;
import com.virohtus.dht.core.engine.store.Store;
import com.virohtus.dht.core.engine.store.peer.PeerStore;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class StabilizationStore implements Store {

    private static final Logger LOG = LoggerFactory.getLogger(StabilizationStore.class);
    private final ExecutorService executorService;
    private final DhtNodeManager dhtNodeManager;
    private final PeerStore peerStore;
    private final NetworkStore networkStore;
    private Future future;

    public StabilizationStore(ExecutorService executorService, DhtNodeManager dhtNodeManager, PeerStore peerStore, NetworkStore networkStore) {
        this.executorService = executorService;
        this.dhtNodeManager = dhtNodeManager;
        this.peerStore = peerStore;
        this.networkStore = networkStore;
    }

    public void start() {
        if(isAlive()) {
            return;
        }
        future = executorService.submit(() -> {
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(DhtProtocol.STABILIZATION_PERIOD);
                    stabilize();
                }
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public void shutdown() {
        if(!isAlive()) {
            return;
        }
        future.cancel(true);
    }

    public boolean isAlive() {
        return future != null && !future.isCancelled() && !future.isDone();
    }

    @Override
    public void onAction(Action action) {
    }


    private void stabilize() {
        try {
//            networkStore.stabilize();
        } catch (Exception e) {
            LOG.error("stabilization error", e);
        }
    }
}
