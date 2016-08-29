package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.event.EventFactory;
import com.virohtus.dht.overlay.node.ConnectionDelegate;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPReceiver implements Runnable {

    private final ConnectionDelegate delegate;
    private final DataInputStream dataInputStream;
    private final EventFactory eventFactory = EventFactory.getInstance();

    public TCPReceiver(ConnectionDelegate delegate, Socket socket) throws IOException {
        this.delegate = delegate;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int dataLength = dataInputStream.readInt();
                byte[] data = new byte[dataLength];
                dataInputStream.readFully(data);
                delegate.onEvent(eventFactory.createEvent(data));
            }
        } catch (Exception e) {
            // if shutdown was triggered intentionally, do not propagate error
            if(!Thread.currentThread().isInterrupted()) {
                delegate.onEvent(new ReceiveError(e));
            }
        }
    }
}
