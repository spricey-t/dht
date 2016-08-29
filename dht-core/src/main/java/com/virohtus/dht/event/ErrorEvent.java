package com.virohtus.dht.event;

public class ErrorEvent extends Event {

    private Exception e;

    public ErrorEvent(Exception e) {
        this.e = e;
    }

    @Override
    public int getType() {
        return EventProtocol.ERROR_EVENT;
    }
}
