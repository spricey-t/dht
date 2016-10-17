package com.virohtus.dht.core.engine.store;

import com.virohtus.dht.core.engine.store.peer.PeerStoreTest;
import com.virohtus.dht.core.engine.store.server.ServerStoreTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PeerStoreTest.class,
        ServerStoreTest.class
})
public class StoreTestSuite {
}
