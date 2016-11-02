package com.virohtus.dht.transport.protocol;

import java.nio.ByteBuffer;

public class Message {

    public static final int VERSION = 1;
    private final ByteBuffer headers;
    private final ByteBuffer payload;

    public Message(ByteBuffer headers, ByteBuffer payload) {
        this.headers = headers;
        this.payload = payload;
    }

    public ByteBuffer getHeaders() {
        return headers;
    }

    public ByteBuffer getPayload() {
        return payload;
    }

    public byte[] serialize() {
        int size = 3*Integer.BYTES + headers.capacity() + payload.capacity();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.putInt(VERSION);
        byteBuffer.putInt(headers.capacity());
        byteBuffer.put(headers);
        byteBuffer.putInt(payload.capacity());
        byteBuffer.put(payload);
        return byteBuffer.array();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!headers.equals(message.headers)) return false;
        return payload.equals(message.payload);

    }

    @Override
    public int hashCode() {
        int result = headers.hashCode();
        result = 31 * result + payload.hashCode();
        return result;
    }
}
