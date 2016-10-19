package com.virohtus.dht.core.action;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActionFactoryTest.class,
        TransportableActionTest.class,
        RequestActionTest.class
})
public class ActionTestSuite {
}
