package com.virohtus.dht.rest.node;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.Peer;
import com.virohtus.dht.node.PeerNotFoundException;
import com.virohtus.dht.rest.serialize.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

@RestController()
@RequestMapping(path = "/node/peers", consumes = "application/json", produces = "application/json")
public class PeerController {

    private static final int QUERY_TIMEOUT_SECONDS = 10;
    @Autowired private ExecutorService dhtExecutor;
    @Autowired private Node node;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Peer> listPeers() {
        return node.listPeers();
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public Peer connect(@RequestBody ConnectionDetails connectionDetails) throws IOException {
        return node.connectToPeer(connectionDetails);
    }

    @RequestMapping(path = "{peerId}", method = RequestMethod.GET)
    public Peer getPeer(@PathVariable String peerId) throws PeerNotFoundException {
        return node.getPeer(peerId);
    }

    @RequestMapping(path = "{peerId}/connection", method = RequestMethod.GET)
    public ConnectionDetails getConnectionDetails(@PathVariable String peerId) throws PeerNotFoundException, InterruptedException, ExecutionException, TimeoutException {
        Peer peer = node.getPeer(peerId);
        Future<ConnectionDetails> connectionDetailsFuture = dhtExecutor.submit(peer::getConnectionDetails);
        return connectionDetailsFuture.get(QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @RequestMapping(path = "{peerId}", method = RequestMethod.DELETE)
    public Peer disconnect(@PathVariable String peerId) throws PeerNotFoundException, InterruptedException, TimeoutException, ExecutionException {
        Future<Peer> peerFuture = dhtExecutor.submit(() -> node.disconnectFromPeer(peerId));
        return peerFuture.get(QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConnectFailure(IOException e) {
        return new ErrorResponse("could not connect to peer", e);
    }

    @ExceptionHandler(PeerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePeerNotFound(PeerNotFoundException e) {
        return new ErrorResponse("could not find peer", e);
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    public ErrorResponse handleTimeoutException(TimeoutException e) {
        return new ErrorResponse("request timed out", e);
    }

    @ExceptionHandler(InterruptedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInterruptedException(InterruptedException e) {
        return new ErrorResponse("request interrupted", e);
    }

    @ExceptionHandler(ExecutionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExecutionException(ExecutionException e) {
        return new ErrorResponse("request aborted", e);
    }
}
