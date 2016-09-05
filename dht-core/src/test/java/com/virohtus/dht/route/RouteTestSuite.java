package com.virohtus.dht.route;

import com.virohtus.dht.route.event.RouteEventTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FingerTableTest.class,
        RouteEventTestSuite.class
})
public class RouteTestSuite {
}
