package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.network.Node;

import java.io.IOException;

public class NodeSerializer extends JsonSerializer<Node> {

    @Override
    public void serialize(Node node, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeObjectField("nodeIdentity", node.getNodeIdentity());
        jsonGenerator.writeObjectField("keyspace", node.getKeyspace());
        jsonGenerator.writeObjectField("fingerTable", node.getFingerTable());

        jsonGenerator.writeEndObject();
    }

}
