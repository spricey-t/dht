package com.virohtus.dht.core.transport.protocol;

import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DhtEvent {
    private Headers headers;
    private byte[] payload;

    public DhtEvent(Headers headers, byte[] payload) {
        this.headers = headers;
        this.payload = payload;
    }

    public DhtEvent(byte[] payload) {
        this.headers = new Headers(DhtProtocol.PROTOCOL_VERSION, payload.length);
        this.payload = payload;
    }

    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream)
        ) {
            outputStream.write(headers.getBytes());
            outputStream.write(payload);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public Headers getHeaders() {
        return headers;
    }

    public byte[] getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DhtEvent event = (DhtEvent) o;

        if (!headers.equals(event.headers)) return false;
        return Arrays.equals(payload, event.payload);

    }

    @Override
    public int hashCode() {
        int result = headers.hashCode();
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
