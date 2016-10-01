package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.transport.protocol.DhtEvent;

public interface ConnectionDelegate {
    void dataReceived(DhtEvent data);
}
