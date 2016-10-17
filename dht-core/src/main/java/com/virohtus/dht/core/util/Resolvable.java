package com.virohtus.dht.core.util;

import java.util.concurrent.TimeoutException;

public class Resolvable<T> {

    private final Object resolveLock = new Object();
    private final long defaultTimeout;
    private T value;

    public Resolvable(long defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public T get() throws InterruptedException, TimeoutException {
        return get(defaultTimeout);
    }

    public T get(long timeout) throws InterruptedException, TimeoutException {
        synchronized (resolveLock) {
            if(!valuePresent()) {
                resolveLock.wait(timeout);
            }
            if(!valuePresent()) {
                throw new TimeoutException();
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
