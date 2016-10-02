package com.virohtus.dht.core.action;

import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.protocol.DhtEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ActionFactory {

    private static final ActionFactory instance = new ActionFactory();

    private ActionFactory() {}

    public static ActionFactory getInstance() {
        return instance;
    }

    public Action createAction(DhtEvent event) throws IOException {
        int type;
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.getPayload());
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            type = inputStream.readInt();
        }

        switch (type) {
        }

        throw new IOException("unsupported TransportableAction type: " + type);
    }
}
