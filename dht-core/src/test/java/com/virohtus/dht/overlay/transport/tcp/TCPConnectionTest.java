package com.virohtus.dht.overlay.transport.tcp;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.event.StringMessageEvent;
import com.virohtus.dht.overlay.node.ConnectionDelegate;
import com.virohtus.dht.overlay.transport.ConnectionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.Socket;

public class TCPConnectionTest {

    @Mock private Socket socket;
    @Mock private ConnectionDelegate connectionDelegate;

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    @Before
    public void initMocks() throws IOException {
        MockitoAnnotations.initMocks(this);
        pipedOutputStream = new PipedOutputStream();
        pipedInputStream = new PipedInputStream(pipedOutputStream);
        mockSocket();
        mockConnectionDelegate();
    }

    private void mockSocket() throws IOException {
        Mockito.when(socket.getInputStream()).thenReturn(pipedInputStream);
        Mockito.when(socket.getOutputStream()).thenReturn(pipedOutputStream);
    }

    private void mockConnectionDelegate() {
    }

    @Test
    public void testReceive() throws IOException {
        TCPConnection tcpConnection = new TCPConnection(ConnectionType.INCOMING, connectionDelegate, socket);

        String testString = "hello world!";
        byte[] testData = testString.getBytes(EventProtocol.STRING_ENCODING);

        DataOutputStream dataOutputStream = new DataOutputStream(pipedOutputStream);
        dataOutputStream.writeInt(testData.length + 8); // write total length
        dataOutputStream.writeInt(EventProtocol.STRING_MESSAGE_EVENT); // write event type
        dataOutputStream.writeInt(testData.length); // write message length
        dataOutputStream.write(testData); // write message
        dataOutputStream.flush();

        final ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(connectionDelegate, Mockito.times(1)).onEvent(idCaptor.capture(), eventCaptor.capture());

        StringMessageEvent expectedEvent = new StringMessageEvent(testString);
        Assert.assertEquals(expectedEvent, eventCaptor.getValue());

        tcpConnection.close();
    }

    @Test(timeout = 5000)
    public void testSend() throws IOException {
        // overwrite mocked input stream so receiver thread doesn't steal incoming data
        Mockito.when(socket.getInputStream()).thenReturn(new PipedInputStream());
        TCPConnection tcpConnection = new TCPConnection(ConnectionType.INCOMING, connectionDelegate, socket);

        String testString = "wat up world!";
        byte[] testData = testString.getBytes(EventProtocol.STRING_ENCODING);

        tcpConnection.send(testData);

        DataInputStream dataInputStream = new DataInputStream(pipedInputStream);
        int receivedDataLength = dataInputStream.readInt();
        byte[] receivedData = new byte[receivedDataLength];
        dataInputStream.readFully(receivedData);

        Assert.assertEquals(testData.length, receivedDataLength);
        Assert.assertEquals(testString, new String(receivedData, EventProtocol.STRING_ENCODING));

        tcpConnection.close();
    }

    @Test(expected = IOException.class)
    public void testInputStreamClosed() throws IOException {
        TCPConnection tcpConnection = new TCPConnection(ConnectionType.INCOMING, connectionDelegate, socket);
        tcpConnection.close();
        Assert.assertFalse(tcpConnection.isAlive());
        pipedInputStream.read();
    }

    @Test(expected = IOException.class)
    public void testOutputStreamClosed() throws IOException {
        TCPConnection tcpConnection = new TCPConnection(ConnectionType.INCOMING, connectionDelegate, socket);
        tcpConnection.close();
        Assert.assertFalse(tcpConnection.isAlive());
        pipedOutputStream.write(2);
    }
}
