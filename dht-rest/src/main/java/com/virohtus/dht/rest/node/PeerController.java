package com.virohtus.dht.rest.node;

import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(path = "/node/peers")
public class PeerController {

    @Autowired private Node node;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Peer> listPeers() {
        return node.listPeers();
    }
}
