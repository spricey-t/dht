package com.virohtus.dht.transport.connection;

import java.util.Arrays;

/**
 * Represents necessary info to create a Connection
 * If any two Connections have identical ConnectionInfo then
 * the Connections are identical
 */
public class ConnectionInfo {

    private final byte[] ipAddress;
    private final int port;

    public ConnectionInfo(byte[] ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionInfo that = (ConnectionInfo) o;

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
    public String toString() {
        byte[] ip = getIpAddress();
        return String.format("%b.%b.%b.%b:%d", ip[0], ip[1], ip[2], ip[3], getPort());
    }
}
