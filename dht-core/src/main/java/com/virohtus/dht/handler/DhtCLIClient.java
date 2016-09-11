package com.virohtus.dht.handler;

import com.virohtus.dht.connection.ConnectionDetails;
import com.virohtus.dht.event.Event;
import com.virohtus.dht.node.Node;
import com.virohtus.dht.node.NodeDelegate;
import com.virohtus.dht.node.Peer;
import com.virohtus.dht.node.PeerNotFoundException;
import com.virohtus.dht.utils.DhtUtilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class DhtCLIClient implements NodeDelegate {

    private static final Map<String, String> usages = new HashMap<>();
    static {
        usages.put("connect",    "connect <server> <port>   - Connects to a peer serving on <server> <port>");
        usages.put("describe",   "describe <peerId>         - Gets connection details for <peerId>");
        usages.put("disconnect", "disconnect <peerId>       - Disconnects from a peer with <peerId>");
        usages.put("get",        "get <peers|>              - Lists <peers> etc ...");
        usages.put("help",       "help                      - Shows this prompt");
        usages.put("quit",       "quit                      - Exits");
    }
    private final Node node;
    private final DhtUtilities dhtUtilities = new DhtUtilities();

    public DhtCLIClient() {
        this.node = new Node();
        this.node.addHandler(this);
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void peerConnected(Peer peer) {
        System.out.println("peer connected: " + peer);
    }

    @Override
    public void peerEventReceived(Peer peer, Event event) {
        System.out.println("received peer event: " + event.getClass().getSimpleName());
    }

    @Override
    public void peerDisconnected(Peer peer) {
        System.out.println("peer disconnected: " + peer);
    }

    public void start() throws IOException {
        node.start();
    }

    public void shutdown() {
        node.shutdown();
        node.waitForCompletion();
    }

    private void handleCommand(String line) {
        String[] args = line.trim().replaceAll(" +", " ").split(" ");
        switch(args[0].toLowerCase()) {
            case "connect":
                handleConnectCommand(args);
                break;
            case "describe":
                handleDescribeCommand(args);
                break;
            case "disconnect":
                handleDisconnectCommand(args);
                break;
            case "get":
                handleGetSubcommand(args);
                break;

            case "help":
                handleHelpCommand(args);
                break;
            case "quit":
                break;
            default:
                System.out.println("unknown command: " + line);
                System.out.println("type 'help' for a list of commands");
        }
    }

    private void handleConnectCommand(String[] args) {
        String usage = "Usage: " + usages.get("connect");
        if(args.length < 3) {
            System.out.println(usage);
            return;
        }
        try {
            int port = Integer.parseInt(args[2]);
            ConnectionDetails connectionDetails = new ConnectionDetails(
                    args[1],
                    port
            );
            Peer peer = node.connectToPeer(connectionDetails);
            System.out.println("connected to peer: " + peer);
        } catch (NumberFormatException e) {
            System.out.println(usage);
            return;
        } catch (IOException e) {
            System.out.println("failed to connect to peer: " + args[1] + ":" + args[2]);
        }
    }

    private void handleDescribeCommand(String[] args) {
        String usage = "Usage: " + usages.get("describe");
        if(args.length < 2) {
            System.out.println(usage);
            return;
        }
        String peerId = args[1];
        Optional<Peer> potentialPeer = node.listPeers().stream().filter(p -> p.getId().equals(peerId)).findFirst();
        String peerNotFound = "could not find peer with id: " + peerId;
        if(!potentialPeer.isPresent()) {
            System.out.println(peerNotFound);
            return;
        }
        Peer peer = potentialPeer.get();
        try {
            ConnectionDetails connectionDetails = peer.getConnectionDetails(node.getId());
            System.out.println(connectionDetails);
        } catch (IOException e) {
            System.out.println("failed to get connection details: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("interrupted wait for connection details: " + e.getMessage());
        }
    }

    private void handleDisconnectCommand(String[] args) {
        String usage = "Usage: " + usages.get("disconnect");
        if(args.length < 2) {
            System.out.println(usage);
            return;
        }
        String peerId = args[1];
        Optional<Peer> potentialPeer = node.listPeers().stream().filter(p -> p.getId().equals(peerId)).findFirst();
        String peerNotFound = "could not find peer with id: " + peerId;
        if(!potentialPeer.isPresent()) {
            System.out.println(peerNotFound);
            return;
        }
        Peer peer = potentialPeer.get();
        try {
            peer = node.disconnectFromPeer(peer.getId());
        } catch (InterruptedException e) {
            System.out.println("interrupted while waiting for peer to disconnect: " + peerId);
        } catch (PeerNotFoundException e) {
            System.out.println(peerNotFound);
        }
    }

    private void handleGetSubcommand(String[] args) {
        String usage = "Usage: " + usages.get("get");
        if(args.length < 2) {
            System.out.println(usage);
            return;
        }
        switch(args[1].toLowerCase()) {
            case "peers":
                node.listPeers().stream().forEach(peer -> System.out.println(peer));
                break;
            default:
                System.out.println(usage);
        }
    }


    public void handleHelpCommand(String[] args) {
        usages.keySet().stream().sorted().forEach(command -> System.out.println(usages.get(command)));
    }

    public static void main(String[] args) {
        DhtCLIClient client = new DhtCLIClient();
        try {
            client.start();
        } catch (IOException e) {
            System.err.println("failed to start client: " + e.getMessage());
            System.exit(1);
        }

        String cmd = "";
        Scanner keyboard = new Scanner(System.in);
        System.out.println("client started on port " + client.getNode().getServerPort() + ". type 'help' to see a list of commands");
        while(!cmd.equalsIgnoreCase("quit")) {
            System.out.print("> ");
            cmd = keyboard.nextLine();
            client.handleCommand(cmd);
        }
        System.out.println("shutting down ...");
        client.shutdown();
    }
}
