package com.virohtus.dht.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EventSerializationUtilities {

    private static final EventSerializationUtilities instance = new EventSerializationUtilities();

    private EventSerializationUtilities() {}

    public static EventSerializationUtilities getInstance() {
        return instance;
    }

    public void writeString(DataOutputStream dataOutputStream, String str) throws IOException {
        byte[] data = str.getBytes(EventProtocol.STRING_ENCODING);
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
    }

    public String readString(DataInputStream dataInputStream) throws IOException {
        int dataLength = dataInputStream.readInt();
        byte[] data = new byte[dataLength];
        dataInputStream.readFully(data);
        return new String(data, EventProtocol.STRING_ENCODING);
    }
}
