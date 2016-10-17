package com.virohtus.dht.core.engine;

import com.virohtus.dht.core.engine.action.EngineActionTestSuite;
import com.virohtus.dht.core.engine.store.StoreTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EngineActionTestSuite.class,
        StoreTestSuite.class,
        SingleThreadedDispatcherTest.class
})
public class EngineTestSuite {
}
