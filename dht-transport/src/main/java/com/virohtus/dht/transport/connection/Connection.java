package com.virohtus.dht.transport.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Connection {

    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

    protected final ConnectionDelegate connectionDelegate;
    protected final ExecutorService executorService;
    private final AtomicBoolean listenerStarted;
    private Future receiverFuture;

    protected Connection(ConnectionDelegate connectionDelegate, ExecutorService executorService) {
        this.connectionDelegate = connectionDelegate;
        this.executorService = executorService;
        this.listenerStarted = new AtomicBoolean(false);
    }

    public abstract void send(byte[] data) throws IOException;
    protected abstract byte[] receive() throws IOException;
    protected abstract void cleanup();

    public void listen() {
        if(isListening()) {
            return;
        }
        synchronized (listenerStarted) {
            listenerStarted.set(false);
            receiverFuture = executorService.submit(this::listenerTask);
            try {
                while(!listenerStarted.get()) {
                    listenerStarted.wait();
                }
            } catch (InterruptedException e) {
                LOG.warn("wait for listener startup interrupted!");
            }
        }
    }

    public boolean isListening() {
        return receiverFuture != null && !receiverFuture.isDone() && !receiverFuture.isCancelled();
    }

    public void close() {
        if(isListening()) {
            receiverFuture.cancel(true);
        }
    }

    private void listenerTask() {
        synchronized (listenerStarted) {
            listenerStarted.set(true);
            listenerStarted.notifyAll();
        }
        try {
            while (!Thread.currentThread().isInterrupted()) {
                connectionDelegate.dataReceived(receive());
            }
        } catch (IOException e) {
            LOG.info("connection disrupted: " + e.getMessage());
        } finally {
            cleanup();
            connectionDelegate.connectionClosed();
        }
    }
}
