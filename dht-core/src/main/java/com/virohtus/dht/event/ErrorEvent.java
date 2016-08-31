package com.virohtus.dht.event;

import java.io.IOException;

public class ErrorEvent extends Event {

    private Exception e;

    public ErrorEvent(byte[] data) throws IOException {
        super(data);
    }

    public ErrorEvent(Exception e) {
        this.e = e;
    }

    @Override
    public int getType() {
        return EventProtocol.ERROR_EVENT;
    }
}
