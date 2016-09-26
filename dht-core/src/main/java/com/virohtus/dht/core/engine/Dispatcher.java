package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.engine.managers.Manager;
import com.virohtus.dht.core.event.Event;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher {

    private final List<Manager> managers;

    public Dispatcher() {
        managers = new ArrayList<>();
    }

    public void dispatch(String peerId, Event event) {
        listManagers().stream().forEach(manager -> manager.handle(peerId, event));
    }

    public List<Manager> listManagers() {
        synchronized (managers) {
            return new ArrayList<>(managers);
        }
    }

    public void registerManager(Manager manager) {
        synchronized (managers) {
            managers.add(manager);
        }
    }

    public void unregisterManager(Manager manager) {
        synchronized (managers) {
            managers.remove(manager);
        }
    }
}
