package com.virohtus.dht.core.transport;

import com.virohtus.dht.core.transport.connection.ConnectionTestSuite;
import com.virohtus.dht.core.transport.io.IOTestSuite;
import com.virohtus.dht.core.transport.protocol.ProtocolTestSuite;
import com.virohtus.dht.core.transport.server.ServerTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConnectionTestSuite.class,
        IOTestSuite.class,
        ProtocolTestSuite.class,
        ServerTestSuite.class
})
public class TransportTestSuite {
}
