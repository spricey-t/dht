package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.net.InetSocketAddress;

public class InetSocketAddressSerializer extends JsonSerializer<InetSocketAddress> {

    @Override
    public void serialize(InetSocketAddress inetSocketAddress, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("host", inetSocketAddress.getHostName());
        jsonGenerator.writeNumberField("port", inetSocketAddress.getPort());

        jsonGenerator.writeEndObject();
    }
}
