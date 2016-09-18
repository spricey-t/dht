package com.virohtus.dht.core.util;

import java.util.UUID;

public class IdUtil {

    public String generateId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
