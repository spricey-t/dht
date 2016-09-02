package com.virohtus.dht;

import com.virohtus.dht.event.EventTestSuite;
import com.virohtus.dht.overlay.transport.tcp.TCPTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventTestSuite.class,
        TCPTestSuite.class
})
public class DhtTestSuite {
}
