package com.virohtus.dht.node;

import java.util.ArrayList;
import java.util.List;

public class FingerTable {

    private boolean isDestabilized = true;
    private final List<String> fingers;

    public FingerTable() {
        fingers = new ArrayList<>();
    }

    public int getSize() {
        synchronized (fingers) {
            return fingers.size();
        }
    }

    public String getFinger(int position) {
        synchronized (fingers) {
            return fingers.get(position);
        }
    }

    public boolean isFinger(String peerId) {
        synchronized (fingers) {
            return fingers.contains(peerId);
        }
    }
}
