package com.virohtus.dht.connection;

import com.virohtus.dht.event.EventSerializable;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.*;

public class ConnectionDetails implements EventSerializable {

    private final DhtUtilities dhtUtilities = DhtUtilities.getInstance();
    private String host;
    private int port;

    public ConnectionDetails() {}

    public ConnectionDetails(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)
        ) {
            host = dhtUtilities.readString(dataInputStream);
            port = dataInputStream.readInt();
        }
    }

    public ConnectionDetails(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionDetails that = (ConnectionDetails) o;

        if (port != that.port) return false;
        return host.equals(that.host);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
        ) {
            dhtUtilities.writeString(host, dataOutputStream);
            dataOutputStream.writeInt(port);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
