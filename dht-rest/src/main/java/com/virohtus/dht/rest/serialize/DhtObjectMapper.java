package com.virohtus.dht.rest.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.key.Keyspace;
import com.virohtus.dht.core.network.NodeIdentity;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.transport.connection.ConnectionInfo;
import com.virohtus.dht.rest.key.KeyspaceSerializer;
import com.virohtus.dht.rest.network.NodeNetworkSerializer;
import com.virohtus.dht.rest.node.ConnectionInfoSerializer;
import com.virohtus.dht.rest.node.DhtNodeSerializer;
import com.virohtus.dht.rest.node.NodeIdentitySerializer;
import com.virohtus.dht.rest.node.PeerSerializer;

public class DhtObjectMapper extends ObjectMapper {

    public DhtObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(NodeIdentity.class, new NodeIdentitySerializer());
        simpleModule.addSerializer(ConnectionInfo.class, new ConnectionInfoSerializer());
        simpleModule.addSerializer(NodeNetwork.class, new NodeNetworkSerializer());
        simpleModule.addSerializer(DhtNode.class, new DhtNodeSerializer());
        simpleModule.addSerializer(Peer.class, new PeerSerializer());
        simpleModule.addSerializer(Keyspace.class, new KeyspaceSerializer());
        this.registerModule(simpleModule);
    }
}
