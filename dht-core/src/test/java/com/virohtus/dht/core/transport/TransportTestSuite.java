package com.virohtus.dht.core.transport;

import com.virohtus.dht.core.transport.server.ServerTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ServerTestSuite.class
})
public class TransportTestSuite {
}
