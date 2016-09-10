package com.virohtus.dht.connection;

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
}
