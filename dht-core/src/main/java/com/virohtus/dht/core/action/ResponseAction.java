package com.virohtus.dht.core.action;

import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;

import java.io.IOException;

public abstract class ResponseAction extends TransportableAction {

    private String requestId;

    public ResponseAction(String requestId) {
        super();
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public ResponseAction(DhtEvent dhtEvent) throws IOException {
        super(dhtEvent);
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        super.toWire(outputStream);
        outputStream.writeSizedData(requestId.getBytes(DhtProtocol.STRING_ENCODING));
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        super.fromWire(inputStream);
        requestId = new String(inputStream.readSizedData(), DhtProtocol.STRING_ENCODING);
    }
}
