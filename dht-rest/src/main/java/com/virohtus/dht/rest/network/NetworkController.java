package com.virohtus.dht.rest.network;

import com.virohtus.dht.core.DhtNode;
import com.virohtus.dht.core.network.GetDhtNetworkFailedException;
import com.virohtus.dht.core.network.NodeNetwork;
import com.virohtus.dht.rest.serialize.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/network")
public class NetworkController {

    @Autowired private DhtNode node;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<NodeNetwork> getNetwork() throws GetDhtNetworkFailedException, InterruptedException {
        return node.getDhtNetwork().getNodeNets();
    }

    @ExceptionHandler(GetDhtNetworkFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGetDhtNetworkFailedException(GetDhtNetworkFailedException e) {
        return new ErrorResponse("could not get dht network", e);
    }
}
