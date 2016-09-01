package com.virohtus.dht;

import com.virohtus.dht.event.Event;
import com.virohtus.dht.event.StringMessageEvent;
import com.virohtus.dht.overlay.node.OverlayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DhtNode extends OverlayNode {

    private static final Logger LOG = LoggerFactory.getLogger(DhtNode.class);

    public DhtNode(int serverPort) {
        super(serverPort);
    }

    @Override
    public void onEvent(String connectionId, Event event) {
        super.onEvent(connectionId, event);
        LOG.info("received event: " + event.getType());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        DhtNode node = new DhtNode(11081);
        DhtNode node = new DhtNode(11082);
        node.start();
        node.connect(InetAddress.getByName("localhost"), 11081);
        Thread.sleep(1000);
        node.getOutgoingConnections().stream().findFirst().get().send( new StringMessageEvent("hello world!").getData());
        node.shutdown();
        node.join();
    }
}
