package com.virohtus.dht.core.transport.server;

import com.virohtus.dht.core.transport.connection.AsyncConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncServer implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncServer.class);
    private final ServerDelegate serverDelegate;
    private final ExecutorService executorService;
    private final AsynchronousServerSocketChannel serverSocketChannel;
    private Future listener;

    public AsyncServer(ServerDelegate serverDelegate, ExecutorService executorService, SocketAddress socketAddress) throws IOException {
        this.serverDelegate = serverDelegate;
        this.executorService = executorService;
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withCachedThreadPool(executorService, 3);
        serverSocketChannel = AsynchronousServerSocketChannel.open(group).bind(socketAddress);
    }

    @Override
    public void listen() {
        final AtomicBoolean started = new AtomicBoolean(false);
        listener = executorService.submit(() -> {
            synchronized (started) {
                started.set(true);
                started.notifyAll();
            }
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    AsynchronousSocketChannel socketChannel = serverSocketChannel.accept().get();
                    serverDelegate.connectionOpened(new AsyncConnection(executorService, socketChannel));
                }
            } finally {
                serverDelegate.serverShutdown();
            }
            return CompletableFuture.completedFuture(null);
        });

        synchronized (started) {
            while(!started.get()) {
                try {
                    started.wait();
                } catch (InterruptedException e) {
                    LOG.error("wait for server startup interrupted!");
                }
            }
        }
    }

    @Override
    public void shutdown() {
        if(!isListening()) {
            return;
        }
//        listener.cancel(true);
        try {
            serverSocketChannel.close();
            listener.get();
        } catch (InterruptedException e) {
            LOG.error("wait for server shutdown interrupted");
        } catch (ExecutionException e) {
            // this is expected
        } catch (IOException e) {
            LOG.warn("could not flush on server shutdown");
        }
    }

    @Override
    public boolean isListening() {
        return listener != null && !listener.isCancelled() && !listener.isDone();
    }

    @Override
    public SocketAddress getSocketAddress() throws IOException {
        return serverSocketChannel.getLocalAddress();
    }
}
