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

    public static Headers deserialize(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            int version = inputStream.readInt();
            int payloadLength = inputStream.readInt();
            return new Headers(version, payloadLength);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Headers headers = (Headers) o;

        if (version != headers.version) return false;
        return payloadLength == headers.payloadLength;

    }

    @Override
    public int hashCode() {
        int result = version;
        result = 31 * result + payloadLength;
        return result;
    }
}
