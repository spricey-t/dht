package com.virohtus.dht.core.key;

import com.virohtus.dht.core.DhtProtocol;

public class KeyspaceService {

    public Keyspace createInitialKeyspace() {
        return new Keyspace(0, DhtProtocol.GLOBAL_KEYSPACE);
    }

    public Keyspace[] splitKeyspaceEqually(Keyspace keyspace) {
        long start = keyspace.getStart();
        long end = keyspace.getEnd();
        long range = end - start;
        long middle = start + range/2;

        Keyspace[] split = new Keyspace[2];
        split[0] = new Keyspace(start, middle);
        split[1] = new Keyspace(middle, end);
        return split;
    }

}
