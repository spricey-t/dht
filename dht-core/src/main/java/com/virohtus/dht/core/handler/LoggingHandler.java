package com.virohtus.dht.core.handler;

import com.virohtus.dht.core.event.Event;
import com.virohtus.dht.core.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingHandler.class);

    @Override
    public void handle(Event event) {
    }

}
