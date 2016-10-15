package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import com.virohtus.dht.core.transport.protocol.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncConnection implements Connection {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncConnection.class);
    private final ExecutorService executorService;
    private final AsynchronousSocketChannel socketChannel;
    private final Object connectionDelegateLock;
    private ConnectionDelegate connectionDelegate;
    private Future listenerFuture;
    private final Object writeLock;

    public AsyncConnection(ExecutorService executorService,
                           AsynchronousSocketChannel socketChannel) {
        this.executorService = executorService;
        this.socketChannel = socketChannel;
        this.connectionDelegateLock = new Object();
        this.writeLock = new Object();
    }

    @Override
    public void listen() {
        if(isListening()) {
            return;
        }
        final AtomicBoolean started = new AtomicBoolean(false);
        listenerFuture = executorService.submit(() -> {
            synchronized (started) {
                started.set(true);
                started.notifyAll();
            }
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] headerData = readSizedData(DhtProtocol.HEADER_SIZE);
                    Headers headers = Headers.deserialize(headerData);
                    byte[] payload = readSizedData(headers.getPayloadLength());
                    DhtEvent event = new DhtEvent(headers, payload);
                    synchronized (connectionDelegateLock) {
                        if (connectionDelegate != null) {
                            connectionDelegate.dataReceived(event);
                        }
                    }
                }
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if(cause == null || !(cause instanceof AsynchronousCloseException)){
                    LOG.info("receiver error: " + e);
                }
            } finally {
                if(connectionDelegate != null) {
                    connectionDelegate.listenerDisrupted();
                }
            }
        });
        synchronized (started) {
            while(!started.get()) {
                try {
                    started.wait();
                } catch (InterruptedException e) {
                    LOG.error("wait for connection listener startup interrupted");
                }
            }
        }
    }

    @Override
    public boolean isListening() {
        return listenerFuture != null && !listenerFuture.isCancelled() && !listenerFuture.isDone();
    }

    @Override
    public void send(DhtEvent event) throws IOException {
        synchronized (writeLock) {
            try {
                socketChannel.write(ByteBuffer.wrap(event.getBytes())).get();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void setConnectionDelegate(ConnectionDelegate connectionDelegate) {
        synchronized (connectionDelegateLock) {
            this.connectionDelegate = connectionDelegate;
        }
    }

    @Override
    public void close() {
        try {
            socketChannel.close();
        } catch (IOException e) {
            LOG.error("io error occurred when closing connection: " + e);
        }
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
