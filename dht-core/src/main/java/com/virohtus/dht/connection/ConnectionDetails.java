package com.virohtus.dht.connection;

import java.util.Arrays;

public class ConnectionDetails {

    private final byte[] ipAddress;
    private final int port;

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
}
