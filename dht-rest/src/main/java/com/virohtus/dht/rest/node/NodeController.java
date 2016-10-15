package com.virohtus.dht.rest.node;

import com.virohtus.dht.core.DhtNodeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/node")
public class NodeController {

    @Autowired private DhtNodeManager node;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public DhtNodeManager getCurrentNode() {
        return node;
    }
}
