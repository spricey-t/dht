package com.virohtus.dht.transport.connection;

public class ConnectionInfo {

    private final String host;
    private final int port;

    public ConnectionInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
