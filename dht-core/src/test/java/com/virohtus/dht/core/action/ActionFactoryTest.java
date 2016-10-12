package com.virohtus.dht.core.action;

import com.virohtus.dht.core.transport.protocol.DhtEvent;
import org.junit.Test;

import java.io.IOException;

public class ActionFactoryTest {

    private final ActionFactory actionFactory = ActionFactory.getInstance();

    @Test(expected = IOException.class)
    public void testCreateUnsupportedAction() throws IOException {
        byte[] actionData = {0, 0, 0, -1};
        DhtEvent dhtEvent = new DhtEvent(actionData);
        actionFactory.createTransportableAction(dhtEvent);
    }

    @Test(expected = IOException.class)
    public void testCreateNoData() throws IOException {
        DhtEvent dhtEvent = new DhtEvent(new byte[0]);
        actionFactory.createTransportableAction(dhtEvent);
    }
}
