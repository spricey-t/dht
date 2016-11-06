package com.virohtus.dht.transport.server;

import com.virohtus.dht.transport.connection.AsyncConnection;
import com.virohtus.dht.transport.connection.ConnectionDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncServer implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncServer.class);
    private final ServerDelegate serverDelegate;
    private final ConnectionDelegate connectionDelegate;
    private final ExecutorService executorService;
    private final AccessPoint accessPoint;
    private final AsynchronousServerSocketChannel serverSocketChannel;
    private final AtomicBoolean shutdownLock = new AtomicBoolean(true);
    private Future listenFuture;

    public AsyncServer(ServerDelegate serverDelegate, ConnectionDelegate connectionDelegate,
                       ExecutorService executorService, AccessPoint accessPoint) throws IOException {
        this.serverDelegate = serverDelegate;
        this.connectionDelegate = connectionDelegate;
        this.executorService = executorService;
        this.accessPoint = accessPoint;
        serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(accessPoint.getPort()));
        InetSocketAddress socketAddress = (InetSocketAddress)serverSocketChannel.getLocalAddress();
        accessPoint.setPort(socketAddress.getPort());
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
                while (!Thread.currentThread().isInterrupted()) {
                    AsynchronousSocketChannel socketChannel = serverSocketChannel.accept().get();
                    serverDelegate.connectionOpened(new AsyncConnection(connectionDelegate, executorService, socketChannel));
                }
            } catch (InterruptedException | ExecutionException e) {
                LOG.warn("server disrupted");
            }
            serverDelegate.serverShutdown();
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
                    LOG.warn("server startup interrupted");
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
                    LOG.warn("server shutdown interrupted");
                }
            }
        }
    }

    @Override
    public boolean isListening() {
        return listenFuture != null && !listenFuture.isCancelled() && !listenFuture.isDone();
    }

    @Override
    public AccessPoint getAccessPoint() {
        return new AccessPoint(accessPoint.getHost(), accessPoint.getPort());
    }
}
