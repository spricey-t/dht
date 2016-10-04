package com.virohtus.dht.core.action;

import com.virohtus.dht.core.engine.action.network.JoinNetworkRequest;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import com.virohtus.dht.core.transport.protocol.DhtProtocol;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class TransportableActionTest {

    private final ActionFactory actionFactory = ActionFactory.getInstance();

    @Test
    public void testSerialize() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
        outputStream.writeInt(DhtProtocol.JOIN_NETWORK_REQUEST);
        outputStream.flush();
        byte[] expected = byteArrayOutputStream.toByteArray();
        JoinNetworkRequest joinNetworkRequest = new JoinNetworkRequest();
        Assert.assertArrayEquals(expected, joinNetworkRequest.serialize());
    }

    @Test
    public void testSerializeDeserialize() throws IOException {
        JoinNetworkRequest joinNetworkRequest = new JoinNetworkRequest();
        byte[] data = joinNetworkRequest.serialize();
        Action reserialized = actionFactory.createAction(new DhtEvent(data));
        Assert.assertEquals(joinNetworkRequest, reserialized);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeserializeIllegalType() throws IOException {
        JoinNetworkRequest joinNetworkRequest = new JoinNetworkRequest();
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        DhtInputStream dhtInputStream = new DhtInputStream(pipedInputStream);
        DataOutputStream outputStream = new DataOutputStream(pipedOutputStream);
        outputStream.writeInt(-1);
        joinNetworkRequest.fromWire(dhtInputStream);
    }
}
