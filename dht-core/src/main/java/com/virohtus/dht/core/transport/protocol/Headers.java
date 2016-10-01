package com.virohtus.dht.core.transport.protocol;

import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Headers {
    private int version;
    private int payloadLength;

    public Headers(int version, int payloadLength) {
        this.version = version;
        this.payloadLength = payloadLength;
    }

    public Headers(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            version = inputStream.readInt();
            payloadLength = inputStream.readInt();
        }
    }

    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream)
        ) {
            outputStream.writeInt(version);
            outputStream.writeInt(payloadLength);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public int getVersion() {
        return version;
    }

    public int getPayloadLength() {
        return payloadLength;
    }
}
