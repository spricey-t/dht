package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.DhtNodeManager;

import java.io.IOException;

public class DhtNodeSerializer extends JsonSerializer<DhtNodeManager> {

    @Override
    public void serialize(DhtNodeManager dhtNodeManager, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

//        jsonGenerator.writeObjectField("nodeIdentity", dhtNodeManager.getNodeIdentity());

        jsonGenerator.writeEndObject();
    }
}
