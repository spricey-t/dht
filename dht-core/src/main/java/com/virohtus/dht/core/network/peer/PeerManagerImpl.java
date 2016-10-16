package com.virohtus.dht.core.network.peer;

import com.virohtus.dht.core.network.NodeIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class PeerManagerImpl implements PeerManager {

    private static final Logger LOG = LoggerFactory.getLogger(PeerManagerImpl.class);
    private final Set<Peer> peers;

    public PeerManagerImpl() {
        peers = new HashSet<>();
    }

    @Override
    public Peer getPeer(String peerId) throws PeerNotFoundException {
        synchronized (peers) {
            for(Peer peer : peers) {
                if(peer.getId().equals(peerId)) {
                    return peer;
                }
            }
            throw new PeerNotFoundException();
        }
    }

    @Override
    public Peer getPeer(NodeIdentity nodeIdentity) throws PeerNotFoundException {
        synchronized (peers) {
            for (Peer peer : peers) {
                try {
                    NodeIdentity peerIdentity = peer.getNodeIdentity();
                    if (peerIdentity.equals(nodeIdentity)) {
                        return peer;
                    }
                } catch(Exception e) {
                    LOG.error("failed to get PeerIdentity!");
                }
            }
            throw new PeerNotFoundException();
        }
    }

    @Override
    public void addPeer(Peer peer) {
        synchronized (peers) {
            peers.add(peer);
        }
    }

    @Override
    public void removePeer(Peer peer) {
        synchronized (peers) {
            peers.remove(peer);
        }
    }

    @Override
    public Set<Peer> getAllPeers() {
        synchronized (peers) {
            return new HashSet<>(peers);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (peers) {
            return peers.isEmpty();
        }
    }

    @Override
    public Set<Peer> clear() {
        synchronized (peers) {
            Set<Peer> tmp = getAllPeers();
            peers.clear();
            return tmp;
        }
    }
}
