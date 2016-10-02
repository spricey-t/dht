package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.action.Action;

public interface Store {
    void onAction(Action action);
}
