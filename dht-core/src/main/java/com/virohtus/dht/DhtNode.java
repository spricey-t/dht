package com.virohtus.dht;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.overlay.node.OverlayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DhtNode extends OverlayNode {

    private static final Logger LOG = LoggerFactory.getLogger(DhtNode.class);

    public DhtNode(int serverPort) {
        super(serverPort);
    }

    @Override
    public void onEvent(Event event) {
        LOG.info("received event: " + event.getType());
    }
}
