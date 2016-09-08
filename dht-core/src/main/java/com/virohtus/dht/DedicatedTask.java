package com.virohtus.dht;

public interface DedicatedTask {
    void start();
    void shutdown();
    void waitForCompletion();
}
