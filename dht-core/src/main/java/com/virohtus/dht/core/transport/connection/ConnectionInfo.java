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

    @Override
    public String toString() {
        return String.format("%s:%d", host, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionInfo that = (ConnectionInfo) o;

        if (port != that.port) return false;
        return host.equals(that.host);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
