package com.virohtus.dht.rest.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.network.NodeNetwork;

import java.io.IOException;

public class NodeNetworkSerializer extends JsonSerializer<NodeNetwork> {

    @Override
    public void serialize(NodeNetwork nodeNetwork, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("predecessor", nodeNetwork.getPredecessor().isPresent() ? nodeNetwork.getPredecessor().get() : null);
        jsonGenerator.writeObjectField("successors", nodeNetwork.getSuccessors());

        jsonGenerator.writeEndObject();
    }

}
