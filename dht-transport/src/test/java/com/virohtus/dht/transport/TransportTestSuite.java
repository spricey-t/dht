package com.virohtus.dht.transport;

import com.virohtus.dht.transport.connection.ConnectionTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DhtInputStreamTest.class,
        DhtOutputStreamTest.class,
        ConnectionTest.class
})
public class TransportTestSuite {
}
