package com.virohtus.dht.rest.node;

import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.overlay.OverlayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/overlay", produces = "application/json", consumes = "application/json")
public class OverlayController {

    @Autowired private Node node;

    @RequestMapping(path =  "", method = RequestMethod.GET)
    public List<OverlayNode> getOverlay() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        // todo controller advice
        return node.getOverlay();
    }

    @RequestMapping(path =  "{nodeId}", method = RequestMethod.GET)
    public OverlayNode getOverlayNode() {
        return node.getOverlayNode();
    }
}
