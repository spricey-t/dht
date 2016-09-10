package com.virohtus.dht.utils;

import java.util.UUID;

public class DhtUtilities {

    private static final DhtUtilities instance = new DhtUtilities();

    private DhtUtilities() {}

    public static DhtUtilities getInstance() {
        return instance;
    }

    public String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String ipAddrToString(byte[] ipAddr) {
        String[] octets = new String[ipAddr.length];
        for(int i = 0; i < ipAddr.length; i++) {
            octets[i] = String.format("%d", ipAddr[i] & 0xff);
        }
        return String.join(".", octets);
    }
}
