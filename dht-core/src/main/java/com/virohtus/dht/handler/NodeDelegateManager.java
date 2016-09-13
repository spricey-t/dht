package com.virohtus.dht.handler;

import com.virohtus.dht.node.NodeDelegate;

import java.util.ArrayList;
import java.util.List;

public class NodeDelegateManager {

    private final List<NodeDelegate> handlers;

    public NodeDelegateManager() {
        handlers = new ArrayList<>();
    }

    public void addHandler(NodeDelegate handler) {
        synchronized (handlers) {
            handlers.add(handler);
        }
    }

    public List<NodeDelegate> listHandlers() {
        synchronized (handlers) {
            return new ArrayList<>(handlers);
        }
    }

    public NodeDelegate removeHandler(NodeDelegate handler) {
        synchronized (handlers) {
            handlers.remove(handler);
            return handler;
        }
    }
}
