package com.virohtus.dht.overlay;

import com.virohtus.dht.overlay.node.NodeTestSuite;
import com.virohtus.dht.overlay.transport.TransportTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TransportTestSuite.class,
        NodeTestSuite.class
})
public class OverlayTestSuite {
}
