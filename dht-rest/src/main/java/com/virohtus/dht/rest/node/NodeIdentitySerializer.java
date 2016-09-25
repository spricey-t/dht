package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.network.NodeIdentity;

import java.io.IOException;

public class NodeIdentitySerializer extends JsonSerializer<NodeIdentity> {

    @Override
    public void serialize(NodeIdentity nodeIdentity, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("nodeId", nodeIdentity.getNodeId());
        jsonGenerator.writeObjectField("connectionInfo", nodeIdentity.getConnectionInfo());
        jsonGenerator.writeObjectField("keyspace", nodeIdentity.getKeyspace());

        jsonGenerator.writeEndObject();
    }

}
