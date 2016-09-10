package com.virohtus.dht.connection.event;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConnectionDetailsResponse extends Event {

    private ConnectionDetails connectionDetails;

    public ConnectionDetailsResponse(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    public ConnectionDetailsResponse(byte[] data) throws IOException {
        super(data);
    }

    @Override
    public int getType() {
        return EventProtocol.CONNECTION_DETAILS_RESPONSE;
    }

    @Override
    protected void serialize(DataOutputStream dataOutputStream) throws IOException {
        super.serialize(dataOutputStream);
        byte[] serializedConnectionDetails = connectionDetails.serialize();
        dataOutputStream.writeInt(serializedConnectionDetails.length);
        dataOutputStream.write(serializedConnectionDetails);
    }

    @Override
    protected void deserialize(DataInputStream dataInputStream) throws IOException {
        super.deserialize(dataInputStream);
        int connectionDetailsLength = dataInputStream.readInt();
        byte[] serializedConnectionDetails = new byte[connectionDetailsLength];
        dataInputStream.readFully(serializedConnectionDetails);
        connectionDetails = new ConnectionDetails(serializedConnectionDetails);
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }
}
