package com.virohtus.dht.transport;

public interface Connection {
    void send(Event event);
    void on(Event event, EventDelegate eventDelegate);
}
