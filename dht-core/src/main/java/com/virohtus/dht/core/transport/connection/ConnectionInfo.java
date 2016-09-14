package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.event.EventSerializable;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ConnectionInfo implements EventSerializable {

    private final String host;
    private final int port;

    public ConnectionInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ConnectionInfo(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream);
        ) {
            host = inputStream.readString();
            port = inputStream.readInt();
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream);
        ) {
            outputStream.writeString(host);
            outputStream.writeInt(port);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
