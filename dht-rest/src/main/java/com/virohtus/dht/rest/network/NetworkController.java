package com.virohtus.dht.rest.network;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.network.NodeNetwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/network")
public class NetworkController {

    @Autowired private DhtNode node;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public NodeNetwork getNetwork() {
        return node.getNodeNetwork();
    }
}
