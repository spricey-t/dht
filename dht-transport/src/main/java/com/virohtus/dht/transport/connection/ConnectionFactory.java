package com.virohtus.dht.transport.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ConnectionFactory {

    public Connection createTcpConnection(ConnectionDelegate connectionDelegate,
                                          ExecutorService executorService,
                                          ConnectionInfo connectionInfo) throws IOException {
        Socket socket = new Socket(connectionInfo.getHost(), connectionInfo.getPort());
        return new TcpConnection(connectionDelegate, executorService, socket);
    }
}
