package com.virohtus.dht;

import com.virohtus.dht.event.HeartbeatEvent;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DhtTest {

    @Test
    public void testDht() throws IOException, InterruptedException {
        DhtNode node1 = new DhtNode(0);
        DhtNode node2 = new DhtNode(0);
        DhtNode node3 = new DhtNode(0);

        node1.start();
        node2.start();
        node3.start();

        node1.connect(InetAddress.getByAddress(node2.getConnectionInfo().getIpAddress()), node2.getConnectionInfo().getPort());
        node2.connect(InetAddress.getByAddress(node3.getConnectionInfo().getIpAddress()), node3.getConnectionInfo().getPort());
        node3.connect(InetAddress.getByAddress(node1.getConnectionInfo().getIpAddress()), node1.getConnectionInfo().getPort());

        node1.initiateHeartbeat();

        node1.join();
        node2.shutdown();
        node3.shutdown();
        node2.join();
        node3.join();
    }
}
