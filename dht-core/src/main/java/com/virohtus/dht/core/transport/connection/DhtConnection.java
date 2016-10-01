package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.transport.protocol.Headers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DhtConnection implements Connection {

    private final ConnectionDelegate connectionDelegate;
    private final ExecutorService executorService;
    private final AsynchronousSocketChannel socketChannel;
    private Future listenerFuture;

    public DhtConnection(ConnectionDelegate connectionDelegate, ExecutorService executorService,
                         AsynchronousSocketChannel socketChannel) {
        this.connectionDelegate = connectionDelegate;
        this.executorService = executorService;
        this.socketChannel = socketChannel;
    }

    @Override
    public void listen() {
        if(isListening()) {
            return;
        }
        listenerFuture = executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] headerData = readSizedData(DhtProtocol.HEADER_SIZE);
                    Headers headers = new Headers(headerData);
                    byte[] payload = readSizedData(headers.getPayloadLength());
                    DhtEvent packet = new DhtEvent(headers, payload);
                    connectionDelegate.dataReceived(packet);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isListening() {
        return false;
    }

    @Override
    public void send(DhtEvent event) throws IOException {
        socketChannel.write(ByteBuffer.wrap(event.getBytes()));
    }

    @Override
    public void close() {
    }

    private byte[] readSizedData(int dataSize) throws ExecutionException, InterruptedException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int totalRead = 0;
        while(totalRead < dataSize) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(dataSize);
            int received = socketChannel.read(byteBuffer).get();
            if(received < 0) {
                throw new IOException("data stream ended prematurely");
            }
            byteArrayOutputStream.write(byteBuffer.array(), 0, received);
            totalRead += received;
        }
        return byteArrayOutputStream.toByteArray();
    }
}
