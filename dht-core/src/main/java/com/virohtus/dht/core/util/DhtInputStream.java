package com.virohtus.dht.core.util;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.EventSerializable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DhtInputStream extends DataInputStream {

    public DhtInputStream(InputStream in) {
        super(in);
    }

    public String readString() throws IOException {
        return new String(readSizedData(), DhtProtocol.STRING_ENCODING);
    }

    public byte[] readSizedData() throws IOException {
        int dataLength = readInt();
        byte[] data = new byte[dataLength];
        readFully(data);
        return data;
    }

    public <T extends EventSerializable> T readEventSerializable(Class<T> clazz) throws IOException {
        byte[] data = readSizedData();
        try {
            Constructor<T> ctor = clazz.getDeclaredConstructor(new Class[] {byte[].class});
            return ctor.newInstance(data);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public <T extends EventSerializable> T readNullableEventSerializable(Class<T> clazz) throws IOException {
        boolean isPresent = readBoolean();
        if(!isPresent) {
            return null;
        }
        return readEventSerializable(clazz);
    }
}
