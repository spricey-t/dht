package com.virohtus.dht.node;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.handler.CoreNodeDelegate;
import com.virohtus.dht.handler.NodeDelegateManager;
import com.virohtus.dht.node.event.GetOverlay;
import com.virohtus.dht.node.overlay.Finger;
import com.virohtus.dht.node.overlay.FingerTable;
import com.virohtus.dht.node.overlay.OverlayNode;
import com.virohtus.dht.server.Server;
import com.virohtus.dht.server.ServerDelegate;
import com.virohtus.dht.utils.DhtUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Node implements ServerDelegate, PeerDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private static final int GET_OVERLAY_TIMEOUT_SECONDS = 200;
    private final DhtUtilities dhtUtilities = new DhtUtilities();
    private final String id;
    private final ExecutorService executorService;
    private final PeerManager peerManager;
    private final NodeDelegateManager nodeDelegateManager;
    private final int requestedServerPort;
    private final CoreNodeDelegate coreNodeDelegate;
    private FingerTable fingerTable;
    private Server server;

    public Node() {
        this(0);
    }

    public Node(int serverPort) {
        this.id = dhtUtilities.generateId();
        this.executorService = Executors.newCachedThreadPool((runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName(this.getClass().getSimpleName());
            return thread;
        });
        this.peerManager = new PeerManager(executorService, this);
        this.nodeDelegateManager = new NodeDelegateManager();
        this.coreNodeDelegate = new CoreNodeDelegate(this, executorService);
        addHandler(coreNodeDelegate);
        this.requestedServerPort = serverPort;
        this.fingerTable = new FingerTable(null, new ArrayList<>());
    }

    @Override
    public void onSocketConnect(Socket socket) {
        try {
            Peer peer = peerManager.createPeer(PeerType.INCOMING, socket);
            // let the server go back to listening for connections - thread will invoke handlers
            executorService.submit(() -> {
                Thread.currentThread().setName(getHandlerThreadName());
                nodeDelegateManager.listHandlers().stream().forEach(handler -> handler.peerConnected(peer));
            });
        } catch (IOException e) {
            LOG.error("failed to create peer");
        }
    }

    @Override
    public void peerEventReceived(Peer peer, Event event) {
        nodeDelegateManager.listHandlers().stream().forEach(handler -> handler.peerEventReceived(peer, event));
    }

    @Override
    public void peerDisconnected(Peer peer) {
        nodeDelegateManager.listHandlers().stream().forEach(handler -> handler.peerDisconnected(peer));
    }

    public void start() throws IOException {
        if(isServerAlive()) {
            return;
        }
        server = new Server(this, executorService, requestedServerPort);
        server.start();
    }

    public void waitForCompletion() {
        if(!isServerAlive()) {
            return;
        }
        server.join();
    }

    public void shutdown() {
        if(!isServerAlive()) {
            return;
        }
        server.shutdown();
        peerManager.shutdown();
        executorService.shutdown();
        try {
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public String getId() {
        return id;
    }

    public int getServerPort() {
        if(!isServerAlive()) {
            return -2;
        }
        return server.getPort();
    }

    public ConnectionDetails getConnectionDetails() {
        // todo throw exception when server is not started yet
        ConnectionDetails connectionDetails = new ConnectionDetails(
                dhtUtilities.ipAddrToString(server.getIpAddress()),
                server.getPort()
        );
        return connectionDetails;
    }

    public Peer connectToPeer(ConnectionDetails connectionDetails) throws IOException {
        Socket socket = new Socket(connectionDetails.getHost(), connectionDetails.getPort());
        Peer peer = peerManager.createPeer(PeerType.OUTGOING, socket);
        nodeDelegateManager.listHandlers().stream().forEach(handler -> handler.connectedToPeer(peer));
        return peer;
    }

    public Peer disconnectFromPeer(String peerId) throws InterruptedException, PeerNotFoundException {
        return peerManager.disconnectFromPeer(peerId);
    }

    public void addHandler(NodeDelegate handler) {
        nodeDelegateManager.addHandler(handler);
    }

    public void removeHandler(NodeDelegate handler) {
        nodeDelegateManager.removeHandler(handler);
    }

    public Peer getPeer(String peerId) throws PeerNotFoundException {
        return peerManager.getPeer(peerId);
    }

    public List<Peer> listPeers() {
        return peerManager.getAllPeers().stream()
                .sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
                .collect(Collectors.toList());
    }

    public Peer getSuccessor(int n) {
        return peerManager.getSuccessor(n);
    }

    public List<Peer> listSuccessors() {
        return peerManager.listSuccessors();
    }

    public FingerTable getFingerTable() {
        return fingerTable;
    }

    public void setFingerTable(FingerTable fingerTable) {
        this.fingerTable = fingerTable;
    }

    public OverlayNode getOverlayNode() {
        /*
        List<Finger> fingerTable = new ArrayList<>();
        peerManager.listSuccessors().stream()
                .forEach(p -> {
                    try {
                        fingerTable.add(new Finger(
                                p.getId(),
                                p.getPeerNodeId(getId()),
                                p.getConnectionDetails(getId())
                        ));
                    } catch (Exception e) {
                        LOG.error("error when generating fingerTable: " + e.getMessage());
                    }
                });
        return new OverlayNode(getId(), getConnectionDetails(), fingerTable);
        */
        return new OverlayNode(getId(), getConnectionDetails(), fingerTable);
    }

    public List<OverlayNode> getOverlay() throws IOException, InterruptedException, TimeoutException, ExecutionException {
        Future<List<OverlayNode>> overlayFuture = executorService.submit(() ->
                coreNodeDelegate.getOverlay()
        );
        return overlayFuture.get(GET_OVERLAY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private boolean isServerAlive() {
        return server != null && server.isAlive();
    }

    private String getHandlerThreadName() {
        return Thread.currentThread().getName() + "-Handler";
    }
}
