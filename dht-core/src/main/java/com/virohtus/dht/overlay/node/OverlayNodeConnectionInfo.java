package com.virohtus.dht.overlay.node;

import com.virohtus.dht.event.EventSerializable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeConnectionInfo implements EventSerializable {

    private final byte[] ipAddress;
    private final int port;

    public OverlayNodeConnectionInfo(byte[] ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public OverlayNodeConnectionInfo(DataInputStream dataInputStream) throws IOException {
        int ipAddressLength = dataInputStream.readInt();
        ipAddress = new byte[ipAddressLength];
        dataInputStream.readFully(ipAddress);
        port = dataInputStream.readInt();
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ) {
            dataOutputStream.writeInt(ipAddress.length);
            dataOutputStream.write(ipAddress);
            dataOutputStream.writeInt(port);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
