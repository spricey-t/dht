package com.virohtus.dht.rest.node;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.Peer;
import com.virohtus.dht.rest.serialize.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping(path = "/node/peers", consumes = "application/json", produces = "application/json")
public class PeerController {

    @Autowired private Node node;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Peer> listPeers() {
        return node.listPeers();
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public Peer connect(@RequestBody ConnectionDetails connectionDetails) throws IOException {
        return node.connectToPeer(connectionDetails);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConnectFailure(IOException e) {
        return new ErrorResponse("could not connect to peer", e);
    }
}
