package com.virohtus.dht.utils;

public class Resolvable<T> {

    private final Object resolveLock = new Object();
    private T value;

    public T get() throws InterruptedException {
        synchronized (resolveLock) {
            if(!valuePresent()) {
                resolveLock.wait();
            }
            return value;
        }
    }

    public void resolve(T resolvedValue) {
        synchronized (resolveLock) {
            value = resolvedValue;
            resolveLock.notifyAll();
        }
    }

    public boolean valuePresent() {
        synchronized (resolveLock) {
            return value != null;
        }
    }

    public void clear() {
        synchronized (resolveLock) {
            value = null;
        }
    }
}
