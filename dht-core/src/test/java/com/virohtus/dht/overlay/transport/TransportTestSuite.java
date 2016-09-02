package com.virohtus.dht.overlay.transport;

import com.virohtus.dht.overlay.transport.tcp.TCPTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConnectionErrorTest.class,
        TCPTestSuite.class
})
public class TransportTestSuite {
}
