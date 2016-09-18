package com.virohtus.dht.core.peer;

import com.virohtus.dht.core.network.NodeIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PeerPool {

    private static final Logger LOG = LoggerFactory.getLogger(PeerPool.class);
    private final Map<String, Peer> pool = new HashMap<>();

    public Peer getPeer(String peerId) throws PeerNotFoundException {
        synchronized (pool) {
            if (pool.containsKey(peerId)) {
                return pool.get(peerId);
            }
            throw new PeerNotFoundException(peerId);
        }
    }

    public Peer getPeer(NodeIdentity nodeIdentity) throws PeerNotFoundException {
        Optional<Peer> potentialPeer = listPeers().stream().filter(p -> {
            try {
                return p.getNodeIdentity().equals(nodeIdentity);
            } catch (InterruptedException e) {
                LOG.warn("get node identity interrupted for peer: " + p.getPeerId());
                return false;
            }
        }).findFirst();

        if(!potentialPeer.isPresent()) {
            throw new PeerNotFoundException(nodeIdentity);
        }
        return potentialPeer.get();
    }

    public void addPeer(Peer peer) {
        synchronized (pool) {
            pool.put(peer.getPeerId(), peer);
        }
    }

    public Peer removePeer(String peerId) throws PeerNotFoundException {
        synchronized (pool) {
            if(pool.containsKey(peerId)) {
                return pool.remove(peerId);
            }
            throw new PeerNotFoundException(peerId);
        }
    }

    public Set<Peer> listPeers() {
        synchronized (pool) {
            return new HashSet<>(pool.values());
        }
    }
}
