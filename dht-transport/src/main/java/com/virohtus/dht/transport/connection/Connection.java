package com.virohtus.dht.transport.connection;

import com.virohtus.dht.core.transport.DhtInputStream;
import com.virohtus.dht.core.transport.DhtOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Connection {

    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

    private final ConnectionDelegate connectionDelegate;
    private final ExecutorService executorService;
    private final Socket socket;
    private final DhtOutputStream outputStream;
    private final DhtInputStream inputStream;
    private Future receiveFuture;

    public Connection(ConnectionDelegate connectionDelegate, ExecutorService executorService, Socket socket) throws IOException {
        this.connectionDelegate = connectionDelegate;
        this.executorService = executorService;
        this.socket = socket;

        outputStream = new DhtOutputStream(socket.getOutputStream());
        inputStream = new DhtInputStream(socket.getInputStream());
    }

    public void listen() {
        if(isListening()) {
            return;
        }
        receiveFuture = executorService.submit(this::receive);
    }

    public boolean isListening() {
        return receiveFuture != null && !receiveFuture.isCancelled() && !receiveFuture.isDone();
    }

    public void send(byte[] data) throws IOException {
        outputStream.writeSizedData(data);
        outputStream.flush();
    }

    public void close() {
        if(isListening()) {
            receiveFuture.cancel(true);
        }
        try {
            socket.close(); // force dataInputStream.read to unblock
        } catch (IOException e) {
            LOG.error("failed to close connection: " + e.getMessage());
        }
    }

    private void receive() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                connectionDelegate.dataReceived(inputStream.readSizedData());
            }
        } catch(IOException e) {
            connectionDelegate.receiveDisrupted(e);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                LOG.error("error occurred when closing connection: " + e.getMessage());
            }
        }
    }
}
