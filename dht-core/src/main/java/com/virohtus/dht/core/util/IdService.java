package com.virohtus.dht.core.util;

import java.util.UUID;

public class IdService {

    public String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
