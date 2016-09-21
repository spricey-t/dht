package com.virohtus.dht.rest.node;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.virohtus.dht.core.peer.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PeerSerializer extends JsonSerializer<Peer> {

    private static final Logger LOG = LoggerFactory.getLogger(PeerSerializer.class);

    @Override
    public void serialize(Peer peer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("peerId", peer.getPeerId());
        try {
            jsonGenerator.writeObjectField("nodeIdentity", peer.getNodeIdentity());
        } catch (InterruptedException e) {
            LOG.warn("wait for peer node identity interrupted when serializing peer: " + peer.getPeerId());
            jsonGenerator.writeObjectField("nodeIdentity", null);
        }
        jsonGenerator.writeStringField("peerType", peer.getPeerType().name());

        jsonGenerator.writeEndObject();
    }

}
