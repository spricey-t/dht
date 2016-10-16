package com.virohtus.dht.rest.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.network.FingerTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FingerTableSerializer extends JsonSerializer<FingerTable> {

    @Override
    public void serialize(FingerTable fingerTable, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

//        jsonGenerator.writeObjectField("predecessor", fingerTable.getPredecessor());
//        jsonGenerator.writeObjectField("successors", fingerTable.getSuccessors());
        jsonGenerator.writeStringField("predecessor", fingerTable.getPredecessor() != null ? fingerTable.getPredecessor().getNodeIdentity().getNodeId() : null);
        List<String> successors = new ArrayList<>();
        fingerTable.getSuccessors().forEach(successor -> successors.add(successor.getNodeIdentity().getNodeId()));
        jsonGenerator.writeObjectField("successors", successors);

        jsonGenerator.writeEndObject();
    }

}
