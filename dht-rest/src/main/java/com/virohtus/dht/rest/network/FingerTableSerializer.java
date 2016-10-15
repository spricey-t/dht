package com.virohtus.dht.rest.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.network.FingerTable;

import java.io.IOException;

public class FingerTableSerializer extends JsonSerializer<FingerTable> {

    @Override
    public void serialize(FingerTable fingerTable, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("predecessor", fingerTable.getPredecessor());
        jsonGenerator.writeObjectField("successors", fingerTable.getSuccessors());

        jsonGenerator.writeEndObject();
    }

}
