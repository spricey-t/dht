package com.virohtus.dht.rest.node;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.network.NodeIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/node")
public class NodeController {

    @Autowired private DhtNode node;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public NodeIdentity getNode() {
        return node.getNodeIdentity();
    }

}
