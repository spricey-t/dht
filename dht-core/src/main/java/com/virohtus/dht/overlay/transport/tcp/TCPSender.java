package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.overlay.node.ConnectionDelegate;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {

    private final String connectionId;
    private final ConnectionDelegate delegate;
    private final DataOutputStream dataOutputStream;

    public TCPSender(String connectionId, ConnectionDelegate delegate, Socket socket) throws IOException {
        this.connectionId = connectionId;
        this.delegate = delegate;
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void send(byte[] data) throws IOException {
        dataOutputStream.writeInt(data.length);
        dataOutputStream.write(data, 0, data.length);
        dataOutputStream.flush();
    }
}
