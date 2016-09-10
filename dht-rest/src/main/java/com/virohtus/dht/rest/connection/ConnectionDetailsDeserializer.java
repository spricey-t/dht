package com.virohtus.dht.rest.connection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.virohtus.dht.connection.ConnectionDetails;

import java.io.IOException;

public class ConnectionDetailsDeserializer extends JsonDeserializer<ConnectionDetails> {

    @Override
    public ConnectionDetails deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return null;
    }

}
