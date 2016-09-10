package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.node.Peer;

import java.io.IOException;

public class PeerSerializer extends JsonSerializer<Peer> {

    @Override
    public void serialize(Peer peer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("peerId", peer.getId());
        jsonGenerator.writeObjectField("peerType", peer.getPeerType());

        jsonGenerator.writeEndObject();
    }

}
