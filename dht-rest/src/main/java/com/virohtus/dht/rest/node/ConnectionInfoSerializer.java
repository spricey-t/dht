package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;

import java.io.IOException;

public class ConnectionInfoSerializer extends JsonSerializer<ConnectionInfo> {

    @Override
    public void serialize(ConnectionInfo connectionInfo, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("host", connectionInfo.getHost());
        jsonGenerator.writeNumberField("port", connectionInfo.getPort());

        jsonGenerator.writeEndObject();
    }

}
