package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.DhtNode;

import java.io.IOException;

public class DhtNodeSerializer extends JsonSerializer<DhtNode> {

    @Override
    public void serialize(DhtNode dhtNode, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("nodeIdentity", dhtNode.getNodeIdentity());

        jsonGenerator.writeEndObject();
    }
}
