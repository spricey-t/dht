package com.virohtus.dht.node.overlay;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.event.EventSerializable;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.*;

public class Finger implements EventSerializable {

    private final DhtUtilities dhtUtilities = new DhtUtilities();
    private String peerId;
    private String peerNodeId;
    private ConnectionDetails connectionDetails;

    public Finger(String peerId, String peerNodeId, ConnectionDetails connectionDetails) {
        this.peerId = peerId;
        this.peerNodeId = peerNodeId;
        this.connectionDetails = connectionDetails;
    }

    public Finger(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        ) {
            peerId = dhtUtilities.readString(dataInputStream);
            peerNodeId = dhtUtilities.readString(dataInputStream);
            connectionDetails = new ConnectionDetails(
                    dhtUtilities.readSizedData(dataInputStream)
            );
        }
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ) {
            dhtUtilities.writeString(peerId, dataOutputStream);
            dhtUtilities.writeString(peerNodeId, dataOutputStream);
            byte[] connectionDetailsData = connectionDetails.serialize();
            dhtUtilities.writeSizedData(connectionDetailsData, dataOutputStream);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public String getPeerId() {
        return peerId;
    }

    public String getPeerNodeId() {
        return peerNodeId;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }
}
