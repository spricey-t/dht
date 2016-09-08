package com.virohtus.dht.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PingEvent extends Event {

    private int originator;

    public PingEvent(int originator) {
        this.originator = originator;
    }

    public PingEvent(byte[] data) throws IOException {
        super(data);
    }

    public int getOriginator() {
        return originator;
    }

    @Override
    public int getType() {
        return EventProtocol.PING_EVENT;
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        originator = dataInputStream.readInt();
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        dataOutputStream.writeInt(originator);
    }
}
