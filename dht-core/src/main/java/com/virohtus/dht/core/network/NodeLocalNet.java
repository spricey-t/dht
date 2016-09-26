package com.virohtus.dht.core.network;

import com.virohtus.dht.core.event.EventSerializable;
import com.virohtus.dht.core.util.DhtInputStream;
import com.virohtus.dht.core.util.DhtOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NodeLocalNet implements EventSerializable {

    private static final Logger LOG = LoggerFactory.getLogger(NodeLocalNet.class);
    private NodeResponsibility responsibility;
    private NodeResponsibility predecessorResponsibility;
    private NodeResponsibility successorResponsibility;

    public NodeLocalNet(NodeResponsibility responsibility) {
        this.responsibility = responsibility;
    }

    public NodeLocalNet(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream dhtInputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            responsibility = dhtInputStream.readEventSerializable(NodeResponsibility.class);
            predecessorResponsibility = dhtInputStream.readNullableEventSerializable(NodeResponsibility.class);
            successorResponsibility = dhtInputStream.readNullableEventSerializable(NodeResponsibility.class);
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream)
        ) {
            outputStream.writeEventSerializable(responsibility);
            outputStream.writeNullableEventSerializable(predecessorResponsibility);
            outputStream.writeNullableEventSerializable(successorResponsibility);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public NodeResponsibility getResponsibility() {
        return responsibility;
    }

    public synchronized NodeResponsibility getPredecessorResponsibility() {
        return predecessorResponsibility;
    }

    public synchronized void setPredecessorResponsibility(NodeResponsibility predecessorResponsibility) {
        this.predecessorResponsibility = predecessorResponsibility;
    }

    public synchronized NodeResponsibility getSuccessorResponsibility() {
        return successorResponsibility;
    }

    public synchronized void setSuccessorResponsibility(NodeResponsibility successorResponsibility) {
        this.successorResponsibility = successorResponsibility;
    }

    public synchronized boolean hasSuccessor() {
        return successorResponsibility != null;
    }
}
