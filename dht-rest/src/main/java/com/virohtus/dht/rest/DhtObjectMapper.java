package com.virohtus.dht.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.virohtus.dht.core.DhtNodeManager;
import com.virohtus.dht.core.network.FingerTable;
import com.virohtus.dht.rest.network.FingerTableSerializer;
import com.virohtus.dht.rest.node.DhtNodeSerializer;
import com.virohtus.dht.rest.node.InetSocketAddressSerializer;

import java.net.InetSocketAddress;

public class DhtObjectMapper extends ObjectMapper {

    public DhtObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addSerializer(DhtNodeManager.class, new DhtNodeSerializer());
        simpleModule.addSerializer(InetSocketAddress.class, new InetSocketAddressSerializer());
        simpleModule.addSerializer(FingerTable.class, new FingerTableSerializer());

        registerModule(simpleModule);
    }
}
