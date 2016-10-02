package com.virohtus.dht.core;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface DhtNode {
    void start() throws ExecutionException, InterruptedException, IOException;
    void shutdown();
}
