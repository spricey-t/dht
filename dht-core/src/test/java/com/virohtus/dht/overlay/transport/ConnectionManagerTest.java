package com.virohtus.dht.overlay.transport;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

public class ConnectionManagerTest {

    private ConnectionManager connectionManager;
    @Mock private Connection connection;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        connectionManager = new ConnectionManager();
        mockConnection();
    }

    private void mockConnection() {
        Mockito.when(connection.getId()).thenReturn(getMockConnectionId());
    }

    private String getMockConnectionId() {
        return "test connection id";
    }

    @Test
    public void testAdd() {
        connectionManager.add(connection);
        Assert.assertEquals(1, connectionManager.list().size());
    }

    @Test
    public void testAddDuplicate() {
        connectionManager.add(connection);
        connectionManager.add(connection);
        Assert.assertEquals(1, connectionManager.list().size());
    }

    @Test
    public void testRemove() {
        connectionManager.add(connection);
        Connection received = connectionManager.remove(connection.getId());
        Assert.assertEquals(0, connectionManager.list().size());
        Assert.assertEquals(connection, received);
    }

    @Test
    public void testRemoveDuplicate() {
        Connection nothing = connectionManager.remove(getMockConnectionId());
        Assert.assertEquals(null, nothing);
    }

    @Test
    public void testList() {
        Set<Connection> expected = new HashSet<>();
        expected.add(connection);

        connectionManager.add(connection);
        Set<Connection> received = connectionManager.list();
        Assert.assertEquals(expected, received);
        Assert.assertEquals(1, received.size());
    }

    @Test
    public void testClear() {
        Set<Connection> expected = new HashSet<>();
        expected.add(connection);

        connectionManager.add(connection);
        Assert.assertEquals(expected, connectionManager.clear());
        Assert.assertEquals(0, connectionManager.list().size());
    }
}
