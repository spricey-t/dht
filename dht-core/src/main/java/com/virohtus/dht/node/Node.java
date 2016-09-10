package com.virohtus.dht.node;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.connection.event.ConnectionDetailsResponse;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.EventProtocol;
import com.virohtus.dht.server.Server;
import com.virohtus.dht.server.ServerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Node implements ServerDelegate, PeerDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final ExecutorService executorService;
    private final PeerManager peerManager;
    private Server server;

    public Node() {
        this.executorService = Executors.newCachedThreadPool((runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName(this.getClass().getSimpleName());
            return thread;
        });
        this.peerManager = new PeerManager(executorService, this);
    }

    @Override
    public void onSocketConnect(Socket socket) {
        try {
            Peer peer = peerManager.createPeer(PeerType.INCOMING, socket);
            LOG.info("peer connected: " + peer.toString());
            try {
                peer.getConnectionDetails();
            } catch(IOException e) {
                LOG.error("could not complete connection details request");
            } catch (InterruptedException e) {
                LOG.error("wait for connection details interrupted");
            }
        } catch (IOException e) {
            LOG.error("failed to create peer");
        }
    }

    @Override
    public void peerEventReceived(Peer peer, Event event) {
        switch (event.getType()) {
            case EventProtocol.CONNECTION_DETAILS_REQUEST:
                sendConnectionDetails(peer);
                break;
        }
    }

    @Override
    public void peerDisconnected(Peer peer) {
        LOG.info("peer disconnected: " + peer.toString());
    }

    public void start() throws IOException {
        if(isServerAlive()) {
            return;
        }
        server = new Server(this, executorService, 0);
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
        waitForCompletion();
    }

    public int getServerPort() {
        if(!isServerAlive()) {
            return -2;
        }
        return server.getPort();
    }

    public void connectToPeer(String server, int port) throws IOException {
        Socket socket = new Socket(server, port);
        Peer peer = new Peer(this, executorService, PeerType.OUTGOING, socket);
        LOG.info("connected to peer: " + peer);
    }

    private boolean isServerAlive() {
        return server != null && server.isAlive();
    }

    private void sendConnectionDetails(Peer peer) {
        ConnectionDetails connectionDetails = server.getConnectionDetails();
        try {
            peer.send(new ConnectionDetailsResponse(connectionDetails));
        } catch (IOException e) {
            LOG.error("failed to send connection details to peer: " + peer + " " + e.getMessage());
            handleBrokenPeer(peer);
        }
    }

    private void handleBrokenPeer(Peer peer) {
        try {
            peer.close();
        } catch (IOException e) {
            LOG.error("failed to fully flush upon close for peer: " + peer + " " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Node node = new Node();
        node.start();

        String cmd = "";
        Scanner keyboard = new Scanner(System.in);
        while(!cmd.equals("quit")) {
            cmd = keyboard.nextLine();
            int port = Integer.parseInt(cmd);
            node.connectToPeer("localhost", port);
        }

        node.shutdown();
        node.waitForCompletion();
    }

}
