package com.virohtus.dht;

import com.virohtus.dht.event.EventTestSuite;
import com.virohtus.dht.overlay.OverlayTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventTestSuite.class,
        OverlayTestSuite.class
})
public class DhtTestSuite {
}
