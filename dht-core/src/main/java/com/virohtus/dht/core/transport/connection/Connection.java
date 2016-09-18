package com.virohtus.dht.core.transport.connection;

import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Connection {
    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
    private final ConnectionDelegate connectionDelegate;
    private final Socket socket;
    private final DhtOutputStream outputStream;
    private final DhtInputStream inputStream;
    private final Future receiveFuture;

    public Connection(ConnectionDelegate connectionDelegate, ExecutorService executorService, Socket socket) throws IOException {
        this.connectionDelegate = connectionDelegate;
        this.socket = socket;
        this.outputStream = new DhtOutputStream(socket.getOutputStream());
        this.inputStream = new DhtInputStream(socket.getInputStream());
        this.receiveFuture = executorService.submit(this::receive);
    }

    public void send(byte[] data) throws IOException {
        outputStream.writeSizedData(data);
        outputStream.flush();
    }

    public void close() {
        receiveFuture.cancel(true);
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
