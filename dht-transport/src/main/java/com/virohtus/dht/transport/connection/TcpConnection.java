package com.virohtus.dht.transport.connection;

import com.virohtus.dht.transport.DhtInputStream;
import com.virohtus.dht.transport.DhtOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class TcpConnection extends Connection {

    private static final Logger LOG = LoggerFactory.getLogger(TcpConnection.class);

    private final Socket socket;
    private final DhtOutputStream outputStream;
    private final DhtInputStream inputStream;

    public TcpConnection(ConnectionDelegate connectionDelegate, ExecutorService executorService, Socket socket) throws IOException {
        super(connectionDelegate, executorService);
        this.socket = socket;

        outputStream = new DhtOutputStream(socket.getOutputStream());
        inputStream = new DhtInputStream(socket.getInputStream());
    }

    @Override
    public void send(byte[] data) throws IOException {
        outputStream.writeSizedData(data);
    }

    @Override
    protected byte[] receive() throws IOException {
        return inputStream.readSizedData();
    }
}
