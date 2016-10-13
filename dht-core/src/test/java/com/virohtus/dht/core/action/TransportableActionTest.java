package com.virohtus.dht.core.action;

import com.virohtus.dht.core.engine.action.network.GetNodeIdentityRequest;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class TransportableActionTest {

    private final ActionFactory actionFactory = ActionFactory.getInstance();

    private TransportableAction transportableAction = new TransportableAction() {
        @Override
        public int getType() {
            return -1;
        }
    };

    @Test
    public void testSerialize() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream);
        outputStream.writeInt(-1);
        outputStream.flush();
        byte[] expected = byteArrayOutputStream.toByteArray();
        Assert.assertArrayEquals(expected, transportableAction.serialize());
    }

    @Test
    public void testSerializeDeserialize() throws IOException {
        GetNodeIdentityRequest request = new GetNodeIdentityRequest();
        byte[] data = request.serialize();
        Action reserialized = actionFactory.createTransportableAction(new DhtEvent(data));
        Assert.assertEquals(request, reserialized);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeserializeIllegalType() throws IOException {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        DhtInputStream dhtInputStream = new DhtInputStream(pipedInputStream);
        DataOutputStream outputStream = new DataOutputStream(pipedOutputStream);
        outputStream.writeInt(-2);
        transportableAction.fromWire(dhtInputStream);
    }
}
