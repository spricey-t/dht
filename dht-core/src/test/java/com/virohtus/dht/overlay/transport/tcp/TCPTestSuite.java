package com.virohtus.dht.overlay.transport.tcp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ReceiveErrorTest.class,
        TCPConnectionTest.class
})
public class TCPTestSuite {
}
