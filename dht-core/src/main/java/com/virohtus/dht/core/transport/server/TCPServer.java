package com.virohtus.dht.core.transport.server;

import com.virohtus.dht.core.event.EventHandler;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.core.transport.server.event.ServerShutdown;
import com.virohtus.dht.core.transport.server.event.ServerStart;
import com.virohtus.dht.core.transport.server.event.SocketConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TCPServer implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(TCPServer.class);
    private final EventHandler eventHandler;
    private final ExecutorService executorService;
    private final Object serverLock;
    private ServerSocket serverSocket;
    private Future serverFuture;
    private Exception startupException;

    public TCPServer(EventHandler handler, ExecutorService executorService) {
        this.eventHandler = handler;
        this.executorService = executorService;
        this.serverLock = new Object();
    }

    @Override
    public void start(int port) throws IOException {
        if(isAlive()) {
            return;
        }
        synchronized (serverLock) {
            startupException = null;
            this.serverFuture = executorService.submit(() -> listen(port));
            try {
                serverLock.wait();
            } catch (InterruptedException e) {
                // someone didn't want to wait for wait()
                Thread.currentThread().interrupt();
            }
            if(startupException != null) {
                throw new IOException(startupException);
            }
        }
    }

    @Override
    public void shutdown() {
        if(!isAlive()) {
            return;
        }
        synchronized (serverLock) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOG.error("failed to fully flush data on server shutdown: " + e.getMessage());
            }
            try {
                serverLock.wait();
            } catch (InterruptedException e) {
                // someone didn't want to wait for wait()
                LOG.warn("server shutdown force stopped when waiting for graceful shutdown");
                Thread.currentThread().interrupt();
            }
        }
    }

    public void join() {
        if(!isAlive()) {
            return;
        }
        synchronized (serverLock) {
            try {
                serverLock.wait();
            } catch (InterruptedException e) {
                // someone didn't want to wait for wait()
                LOG.warn("server join force stopped when waiting for graceful join");
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isAlive() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    @Override
    public ConnectionInfo getConnectionInfo() {
        if(!isAlive()) {
            return null; //todo throw exception?
        }
        try {
            return new ConnectionInfo(
                    InetAddress.getLocalHost().getHostName(), //todo externalize this
                    serverSocket.getLocalPort()
            );
        } catch (UnknownHostException e) {
            LOG.error("could not get hostname of current machine: " + e.getMessage());
            return null; //todo throw exception?
        }
    }

    private void listen(int port) {
        if(isAlive()) {
            return;
        }
        try {
            synchronized (serverLock) {
                serverSocket = new ServerSocket(port);
                serverLock.notifyAll();
            }
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    executorService.submit(() -> eventHandler.handle(new SocketConnect(socket)));
                }
            } catch(IOException e) {
                // serverSocket must have been closed
            }
            synchronized (serverLock) {
                serverLock.notify();
            }
        } catch (IOException e) {
            startupException = e;
            synchronized (serverLock) {
                serverLock.notifyAll();
            }
        }
    }
}
