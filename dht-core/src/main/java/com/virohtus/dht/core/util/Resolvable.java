package com.virohtus.dht.core.util;

public class Resolvable<T> {

    private final Object resolveLock = new Object();
    private final long defaultTimeout;
    private T value;

    public Resolvable(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public T get() throws InterruptedException {
        return get(defaultTimeout);
    }

    public T get(long timeout) throws InterruptedException {
        synchronized (resolveLock) {
            if(!valuePresent()) {
                resolveLock.wait(timeout);
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
