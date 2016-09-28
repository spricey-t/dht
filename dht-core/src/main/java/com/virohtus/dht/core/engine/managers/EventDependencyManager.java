package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.util.Resolvable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EventDependencyManager implements Manager {

    private static final Logger LOG = LoggerFactory.getLogger(EventDependencyManager.class);
    private Map<Integer, Resolvable<Event>> lockMap;

    public EventDependencyManager() {
        lockMap = new HashMap<>();
    }

    @Override
    public void handle(String peerId, Event event) {
        synchronized (lockMap) {
            if(lockMap.containsKey(event.getType())) {
                lockMap.get(event.getType()).resolve(event);
            }
        }
    }

    public Event waitForEvent(int eventType) throws InterruptedException {
        synchronized (lockMap) {
            if(!lockMap.containsKey(eventType)) {
                lockMap.put(eventType, new Resolvable<>(DhtProtocol.NODE_TIMEOUT));
            }
            Event event = lockMap.get(eventType).get();
            lockMap.remove(eventType);
            return event;
        }
    }
}
