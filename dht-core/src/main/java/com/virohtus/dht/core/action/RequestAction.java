package com.virohtus.dht.core.action;

import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.util.IdService;

import java.io.IOException;

public abstract class RequestAction extends TransportableAction {

    private String requestId;

    public RequestAction() {
        super();
        requestId = new IdService().generateId();
    }

    public RequestAction(DhtEvent event) throws IOException {
        super(event);
    }

    public String getRequestId() {
        return requestId;
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
