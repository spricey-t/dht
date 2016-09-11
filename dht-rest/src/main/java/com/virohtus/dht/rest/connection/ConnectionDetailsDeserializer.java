package com.virohtus.dht.rest.connection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.IOException;

public class ConnectionDetailsDeserializer extends JsonDeserializer<ConnectionDetails> {

    private DhtUtilities dhtUtilities = DhtUtilities.getInstance();

    @Override
    public ConnectionDetails deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.readValueAsTree();
        String ipAddress = node.get("ipAddress").asText();
        int port = node.get("port").asInt();
        return new ConnectionDetails(ipAddress, port);
    }

}
