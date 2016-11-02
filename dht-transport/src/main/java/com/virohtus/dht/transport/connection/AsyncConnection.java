package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.protocol.Message;
import com.virohtus.dht.transport.serialize.TransportOutputStream;

import java.io.ByteArrayInputStream;
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
            ByteBuffer byteBuffer = ByteBuffer.wrap(serializeMessage(message));
            while(byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer).get();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public void listen() {
        socketChannel.read();
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

    private Message deserializeMessage() {
    }


    private byte[] readBytes(int length) throws ExecutionException, InterruptedException, IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        while(byteBuffer.hasRemaining()) {
            socketChannel.read(byteBuffer).get();
            if(received < 0) {
                throw new IOException("data stream ended prematurely");
            }
        }
    }

}
