package com.virohtus.dht.node;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.event.EventSerializable;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.*;

public class PeerDetails implements EventSerializable {

    private final DhtUtilities dhtUtilities = new DhtUtilities();

    private String peerNodeId;
    private ConnectionDetails connectionDetails;

    public PeerDetails(String peerNodeId, ConnectionDetails connectionDetails) {
        this.peerNodeId = peerNodeId;
        this.connectionDetails = connectionDetails;
    }

    public PeerDetails(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        ) {
            peerNodeId = dhtUtilities.readString(dataInputStream);
            connectionDetails = new ConnectionDetails(dhtUtilities.readSizedData(dataInputStream));
        }
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ) {
            dhtUtilities.writeString(peerNodeId, dataOutputStream);
            byte[] connectionDetailsData = connectionDetails.serialize();
            dhtUtilities.writeSizedData(connectionDetailsData, dataOutputStream);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public String getPeerNodeId() {
        return peerNodeId;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }
}
