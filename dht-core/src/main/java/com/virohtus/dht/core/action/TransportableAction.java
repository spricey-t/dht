package com.virohtus.dht.core.action;

import com.virohtus.dht.core.network.peer.Peer;
import com.virohtus.dht.core.transport.io.DhtInputStream;
import com.virohtus.dht.core.transport.io.DhtOutputStream;
import com.virohtus.dht.core.transport.protocol.Transportable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class TransportableAction extends Action implements Transportable, Wireable {

    private Peer sourcePeer;

    public TransportableAction() {}
    public TransportableAction(byte[] data) throws IOException {
        deserialize(data);
    }

    public abstract int getType();

    public final Peer getSourcePeer() {
        return sourcePeer;
    }

    public final void setSourcePeer(Peer sourcePeer) {
        this.sourcePeer = sourcePeer;
    }

    public final boolean hasSourcePeer() {
        return sourcePeer != null;
    }

    @Override
    public final byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DhtOutputStream outputStream = new DhtOutputStream(byteArrayOutputStream)
        ) {
            toWire(outputStream);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public final void deserialize(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DhtInputStream inputStream = new DhtInputStream(byteArrayInputStream)
        ) {
            fromWire(inputStream);
        }
    }

    @Override
    public void toWire(DhtOutputStream outputStream) throws IOException {
        outputStream.writeInt(getType());
    }

    @Override
    public void fromWire(DhtInputStream inputStream) throws IOException {
        int type = inputStream.readInt();
        if(type != getType()) {
            throw new IllegalArgumentException(
                    "unmatched TransportableAction types! expected: " + getType() + " received: " + type);
        }
    }

    @Override
    public int hashCode() {
        int result = getType();
        result = 31 * result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TransportableAction)) {
            return false;
        }
        TransportableAction that = (TransportableAction) obj;
        return this.getType() == that.getType();
    }
}
