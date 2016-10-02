package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.action.Action;

public interface Store {
    void onAction(Action action);
}
