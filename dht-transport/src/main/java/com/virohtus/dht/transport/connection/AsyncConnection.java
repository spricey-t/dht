package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncConnection implements Connection {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncConnection.class);
    private final ConnectionDelegate connectionDelegate;
    private final ExecutorService executorService;
    private final AsynchronousSocketChannel socketChannel;
    private final AtomicBoolean shutdownLock;
    private Future listenFuture;

    public AsyncConnection(ConnectionDelegate connectionDelegate, ExecutorService executorService, AsynchronousSocketChannel socketChannel) {
        this.connectionDelegate = connectionDelegate;
        this.executorService = executorService;
        this.socketChannel = socketChannel;
        this.shutdownLock = new AtomicBoolean(true);
    }

    @Override
    public void send(Message message) throws ConnectionException {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(message.serialize());
            while(byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new ConnectionException("message send failed!", e);
        }
    }

    @Override
    public void listen() {
        if(isListening()) {
            return;
        }

        shutdownLock.set(false);
        final AtomicBoolean startupLock = new AtomicBoolean(false);
        listenFuture = executorService.submit(() -> {
            synchronized (startupLock) {
                startupLock.set(true);
                startupLock.notifyAll();
            }
            try {
                while(!Thread.currentThread().isInterrupted()) {
                    connectionDelegate.receive(deserializeMessage());
                }
            } catch (Exception e) {
                connectionDelegate.receiverError(e);
            }
            synchronized (shutdownLock) {
                shutdownLock.set(true);
                shutdownLock.notifyAll();
            }
        });

        synchronized (startupLock) {
            while(!startupLock.get()) {
                try {
                    startupLock.wait();
                } catch (InterruptedException e) {
                    LOG.warn("listen startup interrupted");
                }
            }
        }
    }

    @Override
    public void shutdown() {
        if(!isListening()) {
            return;
        }
        listenFuture.cancel(true);
        synchronized (shutdownLock) {
            while(!shutdownLock.get()) {
                try {
                    shutdownLock.wait();
                } catch (InterruptedException e) {
                    LOG.warn("listen shutdown interrupted");
                }
            }
        }
    }

    @Override
    public boolean isListening() {
        return listenFuture != null && !listenFuture.isDone() && !listenFuture.isCancelled();
    }


    private Message deserializeMessage() throws ConnectionException {
        try {
            int protocolVersion = readBytes(Integer.BYTES).getInt();
            if(protocolVersion != Message.VERSION) {
                throw new IllegalArgumentException("Unsupported message version! " + protocolVersion);
            }
            ByteBuffer headerData = readSizedData();
            ByteBuffer payloadData = readSizedData();
            return new Message(headerData, payloadData);
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw new ConnectionException("message deserialization failed!", e);
        }
    }

    private ByteBuffer readSizedData() throws InterruptedException, ExecutionException, IOException {
        int dataSize = readBytes(Integer.BYTES).getInt();
        return readBytes(dataSize);
    }

    private ByteBuffer readBytes(int length) throws ExecutionException, InterruptedException, IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        while(byteBuffer.hasRemaining()) {
            socketChannel.read(byteBuffer).get();
        }
        byteBuffer.flip();
        return byteBuffer;
    }

}
