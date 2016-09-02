package com.virohtus.dht.event;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventTest.class,
        ErrorEventTest.class,
        EventFactoryTest.class,
        StringMessageEventTest.class
})
public class EventTestSuite {
}
