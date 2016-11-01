package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.protocol.Message;

public interface ConnectionDelegate {
    void receive(Message message);
    void receiverError(Exception e);
}
