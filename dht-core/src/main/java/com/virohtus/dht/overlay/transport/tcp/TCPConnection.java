package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.overlay.node.ConnectionDelegate;
import com.virohtus.dht.overlay.transport.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class TCPConnection extends Connection {

    private static final Logger LOG = LoggerFactory.getLogger(TCPConnection.class);
    private final TCPReceiver receiver;
    private final TCPSender sender;
    private Thread receiverThread;

    public TCPConnection(ConnectionDelegate delegate, Socket socket) throws IOException {
        super(delegate, socket);
        receiver = new TCPReceiver(delegate, socket);
        sender = new TCPSender(delegate, socket);
        receiverThread = new Thread(receiver);
    }

    @Override
    public void send(byte[] data) throws IOException {
        sender.send(data);
    }

    @Override
    public void close() {
        try {
            if(receiverThread != null && receiverThread.isAlive()) {
                receiverThread.interrupt();
            }
            socket.close();
        } catch (IOException e) {
            LOG.error("Error on TCPConnection close: " + e.getMessage());
        }
    }

}
