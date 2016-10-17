package com.virohtus.dht.core.action;

import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.IOException;

public interface Wireable {
    void toWire(DhtOutputStream outputStream) throws IOException;
    void fromWire(DhtInputStream inputStream) throws IOException;
}
