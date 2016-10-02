package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.action.Action;

import java.util.List;

public interface Dispatcher {
    void dispatch(Action action);
    void start();
    void shutdown();
    boolean isAlive();
    void registerStore(Store store);
    void unregisterStore(Store store);
    List<Store> listStores();
}
