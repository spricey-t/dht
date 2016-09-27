package com.virohtus.dht.core.engine.managers;

import com.virohtus.dht.core.DhtProtocol;
import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.RequestEvent;
import com.virohtus.dht.core.event.ResponseEvent;
import com.virohtus.dht.core.peer.Peer;
import com.virohtus.dht.core.util.Resolvable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager implements Manager {

    private final Map<String, Resolvable<ResponseEvent>> requestMap;

    public RequestManager() {
        requestMap = new ConcurrentHashMap<>();
    }

    @Override
    public void handle(String peerId, Event event) {
        if(event instanceof ResponseEvent) {
            ResponseEvent responseEvent = (ResponseEvent) event;
            if(requestMap.containsKey(responseEvent.getRequestId())) {
                requestMap.remove(responseEvent.getRequestId()).resolve(responseEvent);
            }
        }
    }

    public <T extends ResponseEvent> T submitRequest(Peer peer, RequestEvent request, Class<T> clazz) throws InterruptedException, IOException {
        Resolvable<T> responseResolvable;
        peer.send(request);
        if(!requestMap.containsKey(request.getRequestId())) {
            requestMap.put(request.getRequestId(), new Resolvable<>(DhtProtocol.NODE_TIMEOUT));
        }
        responseResolvable = (Resolvable<T>) requestMap.get(request.getRequestId());
        return responseResolvable.get();
    }
}
