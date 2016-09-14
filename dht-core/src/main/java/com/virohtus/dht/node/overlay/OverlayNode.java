package com.virohtus.dht.node.overlay;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.event.EventSerializable;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Serialized Model
 * int nodeIdDataSize
 * byte[nodeIdDataSize] nodeIdData
 * int connectionDetailsDataSize
 * byte[connectionDetailsDataSize] connectionDetailsData
 * int fingerTableCount
 * loop(fingerTableCount)
 *      int fingerDataSize
 *      byte[fingerDataSize] fingerData
 */
public class OverlayNode implements EventSerializable {

    private final DhtUtilities dhtUtilities = new DhtUtilities();
    private String nodeId;
    private ConnectionDetails connectionDetails;
    private FingerTable fingerTable;

    public OverlayNode(String nodeId, ConnectionDetails connectionDetails, FingerTable fingerTable) {
        this.nodeId = nodeId;
        this.connectionDetails = connectionDetails;
        this.fingerTable = fingerTable;
    }

    public OverlayNode(byte[] data) throws IOException {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        ) {
            nodeId = dhtUtilities.readString(dataInputStream);
            connectionDetails = new ConnectionDetails(dhtUtilities.readSizedData(dataInputStream));
            fingerTable = new FingerTable(dhtUtilities.readSizedData(dataInputStream));
        }
    }

    @Override
    public byte[] serialize() throws IOException {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        ) {
            dhtUtilities.writeString(nodeId, dataOutputStream);
            dhtUtilities.writeSizedData(connectionDetails.serialize(), dataOutputStream);
            dhtUtilities.writeSizedData(fingerTable.serialize(), dataOutputStream);
            dataOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    public String getNodeId() {
        return nodeId;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }
}
