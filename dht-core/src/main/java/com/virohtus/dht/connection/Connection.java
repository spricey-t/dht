package com.virohtus.dht.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Connection {

    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
    private final ConnectionDelegate connectionDelegate;
    private final Socket socket;
    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;
    private final Future receiveFuture;

    public Connection(ConnectionDelegate connectionDelegate, ExecutorService executorService, Socket socket) throws IOException {
        this.connectionDelegate = connectionDelegate;
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.receiveFuture = executorService.submit(this::receive);
    }

    public void send(byte[] data) throws IOException {
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data);
        dataOutputStream.flush();
    }

    public void close() throws IOException {
        synchronized (receiveFuture) {
            if(!receiveFuture.isCancelled() && !receiveFuture.isDone()) {
                receiveFuture.cancel(true);
                try {
                    receiveFuture.wait();
                } catch (InterruptedException e) {
                    // someone didn't want to wait for wait()
                    LOG.warn("connection force closed when waiting to close gracefully");
                    Thread.currentThread().interrupt();
                }
            }
        }
        dataOutputStream.close();
        dataInputStream.close();
        socket.close();
    }

    private void receive() {
        Thread.currentThread().setName(Thread.currentThread().getName() + "-" + getClass().getSimpleName());
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int dataLength = dataInputStream.readInt();
                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data);
                connectionDelegate.dataReceived(data);
            }
        } catch(IOException e) {
            connectionDelegate.receiveDisrupted(e);
        }
        synchronized (receiveFuture) {
            receiveFuture.notifyAll();
        }
    }
}
