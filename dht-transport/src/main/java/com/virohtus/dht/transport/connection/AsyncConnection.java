package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.protocol.Message;
import com.virohtus.dht.transport.serialize.TransportOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class AsyncConnection implements Connection {

    private final ExecutorService executorService;
    private final AsynchronousSocketChannel socketChannel;

    public AsyncConnection(ExecutorService executorService, AsynchronousSocketChannel socketChannel) {
        this.executorService = executorService;
        this.socketChannel = socketChannel;
    }

    @Override
    public void send(Message message) throws ConnectionException {
        try {
            byte[] data = serializeMessage(message);
            int bytesWritten = 0;
            while(bytesWritten < data.length) {
                bytesWritten += socketChannel.write(ByteBuffer.wrap(data)).get();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public void listen() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean isListening() {
        return false;
    }


    private byte[] serializeMessage(Message message) throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            TransportOutputStream outputStream = new TransportOutputStream(byteArrayOutputStream)
        ) {
            outputStream.writeInt(Message.VERSION);
            outputStream.writeSizedData(message.getHeaders());
            outputStream.writeSizedData(message.getPayload());
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    private void deserializeMessage() {
    }

}
