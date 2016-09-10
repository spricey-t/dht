package com.virohtus.dht.connection;

import com.virohtus.dht.event.EventSerializable;

import java.io.*;
import java.util.Arrays;

public class ConnectionDetails implements EventSerializable {

    private final byte[] ipAddress;
    private final int port;

    public ConnectionDetails(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)
        ) {
            int ipAddrLength = dataInputStream.readInt();
            ipAddress = new byte[ipAddrLength];
            dataInputStream.readFully(ipAddress);
            port = dataInputStream.readInt();
        }
    }

    public ConnectionDetails(byte[] ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public byte[] getIpAddress() {
        return ipAddress.clone();
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
        return Arrays.equals(ipAddress, that.ipAddress);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(ipAddress);
        result = 31 * result + port;
        return result;
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)
        ) {
            dataOutputStream.write(ipAddress.length);
            dataOutputStream.write(ipAddress);
            dataOutputStream.writeInt(port);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d:%d", ipAddress[0], ipAddress[1], ipAddress[2], ipAddress[3], port);
    }
}
