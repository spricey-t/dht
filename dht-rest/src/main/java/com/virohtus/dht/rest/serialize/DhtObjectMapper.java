package com.virohtus.dht.rest.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.transport.connection.ConnectionInfo;
import com.virohtus.dht.rest.network.NodeNetworkSerializer;
import com.virohtus.dht.rest.node.ConnectionInfoSerializer;
import com.virohtus.dht.rest.node.NodeIdentitySerializer;

public class DhtObjectMapper extends ObjectMapper {

    public DhtObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(NodeIdentity.class, new NodeIdentitySerializer());
        simpleModule.addSerializer(ConnectionInfo.class, new ConnectionInfoSerializer());
        simpleModule.addSerializer(NodeNetwork.class, new NodeNetworkSerializer());
        this.registerModule(simpleModule);
    }
}
