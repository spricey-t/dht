package com.virohtus.dht.rest.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.Peer;
import com.virohtus.dht.rest.node.NodeSerializer;
import com.virohtus.dht.rest.node.PeerSerializer;

public class DhtObjectMapper extends ObjectMapper {

    public DhtObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Node.class, new NodeSerializer());
        simpleModule.addSerializer(Peer.class, new PeerSerializer());
        this.registerModule(simpleModule);
    }
}
