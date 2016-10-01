package com.virohtus.dht.core.transport;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class ReceiveContext {

    private final AsynchronousSocketChannel socketChannel;
    private final ByteBuffer byteBuffer;

    public ReceiveContext(AsynchronousSocketChannel socketChannel, ByteBuffer byteBuffer) {
        this.socketChannel = socketChannel;
        this.byteBuffer = byteBuffer;
    }

    public AsynchronousSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}
