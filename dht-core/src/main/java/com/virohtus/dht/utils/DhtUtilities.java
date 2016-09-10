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
}
