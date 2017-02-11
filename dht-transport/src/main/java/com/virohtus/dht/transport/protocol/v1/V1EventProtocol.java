package com.virohtus.dht.transport.protocol.v1;

import com.virohtus.dht.transport.protocol.EventProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class V1EventProtocol implements EventProtocol {

    private static final Logger LOG = LoggerFactory.getLogger(V1EventProtocol.class);

    @Override
    public int getVersion() {
        return EventProtocol.V1;
    }

    @Override
    public byte[] generateHeaders() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ) {
            dataOutputStream.writeInt(getVersion());
            return byteArrayOutputStream.toByteArray();
        }
    }

}
