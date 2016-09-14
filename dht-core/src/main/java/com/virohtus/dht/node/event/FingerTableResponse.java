package com.virohtus.dht.node.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.node.overlay.FingerTable;

import java.io.IOException;

public class FingerTableResponse extends Event {

    private FingerTable fingerTable;

    public FingerTableResponse(String initiatingNodeId, FingerTable fingerTable) {
        super(initiatingNodeId);
        this.fingerTable = fingerTable;
    }

    public FingerTableResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.FINGER_TABLE_RESPONSE;
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }
}
