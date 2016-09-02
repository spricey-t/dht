package com.virohtus.dht.overlay;

import com.virohtus.dht.overlay.node.NodeTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        OverlayTestSuite.class,
        NodeTestSuite.class
})
public class OverlayTestSuite {
}
