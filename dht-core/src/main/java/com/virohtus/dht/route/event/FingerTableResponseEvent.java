package com.virohtus.dht.route.event;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.route.FingerTable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FingerTableResponseEvent extends Event {

    private FingerTable fingerTable;

    public FingerTableResponseEvent(FingerTable fingerTable) {
        this.fingerTable = fingerTable;
    }

    public FingerTableResponseEvent(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.FINGER_TABLE_RESPONSE_EVENT;
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        fingerTable.deserialize(dataInputStream);
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        fingerTable.serialize(dataOutputStream);
    }
}
