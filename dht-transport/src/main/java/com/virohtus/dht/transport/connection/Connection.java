package com.virohtus.dht.transport.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class Connection {

    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

    protected final ConnectionDelegate connectionDelegate;
    protected final ExecutorService executorService;
    private Future receiverFuture;

    protected Connection(ConnectionDelegate connectionDelegate, ExecutorService executorService) {
        this.connectionDelegate = connectionDelegate;
        this.executorService = executorService;
    }

    public abstract void send(byte[] data) throws IOException;
    protected abstract byte[] receive() throws IOException;

    public void listen() {
        if(isListening()) {
            return;
        }
        receiverFuture = executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    connectionDelegate.dataReceived(receive());
                }
            } catch (IOException e) {
                connectionDelegate.receiveDisrupted(e);
            }
        });
    }

    public boolean isListening() {
        return receiverFuture != null && !receiverFuture.isDone() && !receiverFuture.isCancelled();
    }

    public void close() {
        if(isListening()) {
            receiverFuture.cancel(true);
        }
    }
}
