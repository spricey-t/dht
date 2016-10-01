package com.virohtus.dht.core.transport.protocol;

import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DhtEvent {
    private Headers headers;
    private byte[] payload;

    public DhtEvent(Headers headers, byte[] payload) {
        this.headers = headers;
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
}
