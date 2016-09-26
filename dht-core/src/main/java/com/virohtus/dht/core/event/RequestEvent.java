package com.virohtus.dht.core.event;

import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;
import com.virohtus.dht.core.util.IdUtil;

import java.io.IOException;

public abstract class RequestEvent extends Event {

    private String requestId;

    public RequestEvent() {
        requestId = new IdUtil().generateId();
    }

    public RequestEvent(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public void serialize(DhtOutputStream dhtOutputStream) throws IOException {
        super.serialize(dhtOutputStream);
        dhtOutputStream.writeString(requestId);
    }

    @Override
    public void deserialize(DhtInputStream dhtInputStream) throws IOException {
        super.deserialize(dhtInputStream);
        requestId = dhtInputStream.readString();
    }

    public String getRequestId() {
        return requestId;
    }
}
