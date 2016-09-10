package com.virohtus.dht.rest.connection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.IOException;

public class ConnectionDetailsSerializer extends JsonSerializer<ConnectionDetails> {

    private DhtUtilities dhtUtilities = DhtUtilities.getInstance();

    @Override
    public void serialize(ConnectionDetails connectionDetails, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("ipAddress", dhtUtilities.ipAddrToString(connectionDetails.getIpAddress()));
        jsonGenerator.writeNumberField("port", connectionDetails.getPort());

        jsonGenerator.writeEndObject();
    }

}
