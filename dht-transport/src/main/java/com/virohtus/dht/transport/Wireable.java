package com.virohtus.dht.transport;

import com.virohtus.dht.transport.serialize.TransportInputStream;
import com.virohtus.dht.transport.serialize.TransportOutputStream;

public interface Wireable {
    void toWire(final TransportOutputStream outputStream);
    void fromWire(final TransportInputStream inputStream);
}
