package com.virohtus.dht.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HeartbeatEvent extends Event {

    private String startingOverlayNodeId;

    public HeartbeatEvent(String startingOverlayNodeId) {
        this.startingOverlayNodeId = startingOverlayNodeId;
    }

    public HeartbeatEvent(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.HEARTBEAT_EVENT;
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        int dataLen = dataInputStream.readInt();
        byte[] data = new byte[dataLen];
        dataInputStream.readFully(data);
        startingOverlayNodeId = new String(data, EventProtocol.STRING_ENCODING);
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        byte[] data = startingOverlayNodeId.getBytes(EventProtocol.STRING_ENCODING);
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
        dataOutputStream.flush();
    }
}
