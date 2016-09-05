package com.virohtus.dht.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringMessageEvent extends Event {

    private String message;

    public StringMessageEvent(String message) {
        this.message = message;
    }

    public StringMessageEvent(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.STRING_MESSAGE_EVENT;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        message = eventSerializationUtilities.readString(dataInputStream);
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        eventSerializationUtilities.writeString(dataOutputStream, message);
        dataOutputStream.flush();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringMessageEvent that = (StringMessageEvent) o;

        return message != null ? message.equals(that.message) : that.message == null;

    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }
}
