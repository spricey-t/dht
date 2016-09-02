package com.virohtus.dht.overlay.node;

import com.virohtus.dht.overlay.transport.Connection;
import com.virohtus.dht.overlay.transport.ConnectionManager;
import com.virohtus.dht.overlay.transport.ConnectionType;
import com.virohtus.dht.overlay.transport.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

public class OverlayNodeTest {

    @InjectMocks private OverlayNode overlayNode = new OverlayNode(0){};
    @Mock private Server server;
    @Spy private ConnectionManager connectionManager;
    @Mock private Connection connection;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        mockServer();
        mockConnection();
    }

    private void mockServer() {
    }

    private void mockConnection() {
        Mockito.when(connection.getConnectionType()).thenReturn(ConnectionType.INCOMING);
        Mockito.when(connection.getId()).thenReturn(getMockConnectionId());
    }

    private String getMockConnectionId() {
        return "mock connection id";
    }

    @Test
    public void testStart() {
        overlayNode.start();
        Mockito.verify(server, Mockito.times(1)).start();
    }

    @Test
    public void testShutdown() throws InterruptedException {
        overlayNode.start();
        connectionManager.add(connection);
        overlayNode.shutdown();

        Assert.assertEquals(0, connectionManager.list().size());
        Mockito.verify(server, Mockito.times(1)).shutdown();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void testConnect() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        overlayNode.connect(InetAddress.getByName("localhost"), port);

        Assert.assertEquals(1, overlayNode.getOutgoingConnections().size());
        Connection connection = overlayNode.getOutgoingConnections().stream().findAny().get();
        Assert.assertEquals(ConnectionType.OUTGOING, connection.getConnectionType());
        overlayNode.shutdown();
        serverSocket.close();
    }

    @Test
    public void testConnectionFailure() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(0);
        int port = serverSocket.getLocalPort();
        overlayNode.connect(InetAddress.getByName("localhost"), port);
        Connection connection = overlayNode.getOutgoingConnections().stream().findAny().get();
        serverSocket.close();

        Thread.sleep(200);
        Assert.assertEquals(0, connectionManager.list().size());

        overlayNode.shutdown();
    }

    @Test
    public void testServerShutdown() {
        connectionManager.add(connection);
        overlayNode.onServerError(new Exception("bogus"));

        Assert.assertEquals(0, connectionManager.list().size());
        Mockito.verify(server, Mockito.times(1)).shutdown();
        Mockito.verify(connection, Mockito.times(1)).close();
    }


    @Test(timeout = 10000)
    public void testIncomingConnections() throws InterruptedException, IOException {
        OverlayNode overlayNode = new OverlayNode(0){};
        overlayNode.start();

        Thread.sleep(200);
        Socket socket = new Socket("localhost", overlayNode.getServerPort());
        Thread.sleep(200);

        Set<Connection> connections = overlayNode.getIncomingConnections();
        Assert.assertEquals(1, connections.size());
        Connection connection = connections.stream().findAny().get();
        Assert.assertEquals(ConnectionType.INCOMING, connection.getConnectionType());

        overlayNode.shutdown();
        overlayNode.join();
    }

}
