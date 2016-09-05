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

    public String getStartingOverlayNodeId() {
        return startingOverlayNodeId;
    }

    @Override
    public int getType() {
        return EventProtocol.HEARTBEAT_EVENT;
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        startingOverlayNodeId = eventSerializationUtilities.readString(dataInputStream);
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        eventSerializationUtilities.writeString(dataOutputStream, startingOverlayNodeId);
        dataOutputStream.flush();
    }
}
