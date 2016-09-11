package com.virohtus.dht.connection;

import com.virohtus.dht.event.EventSerializable;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.*;
import java.util.Arrays;

public class ConnectionDetails implements EventSerializable {

    private final DhtUtilities dhtUtilities = DhtUtilities.getInstance();
    private String ipAddress;
    private int port;

    public ConnectionDetails() {}

    public ConnectionDetails(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)
        ) {
            ipAddress = dhtUtilities.readString(dataInputStream);
            port = dataInputStream.readInt();
        }
    }

    public ConnectionDetails(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
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
        return ipAddress.equals(that.ipAddress);

    }

    @Override
    public int hashCode() {
        int result = ipAddress.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
        ) {
            dhtUtilities.writeString(ipAddress, dataOutputStream);
            dataOutputStream.writeInt(port);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public String toString() {
        return ipAddress + ":" + port;
    }
}
