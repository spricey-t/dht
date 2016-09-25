package com.virohtus.dht.rest.key;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.key.Keyspace;

import java.io.IOException;

public class KeyspaceSerializer extends JsonSerializer<Keyspace> {

    @Override
    public void serialize(Keyspace keyspace, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("start", keyspace.getStart());
        jsonGenerator.writeNumberField("end", keyspace.getEnd());

        jsonGenerator.writeEndObject();
    }

}
