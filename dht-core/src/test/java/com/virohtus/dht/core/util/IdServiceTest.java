package com.virohtus.dht.core.util;

import org.junit.Assert;
import org.junit.Test;

public class IdServiceTest {

    @Test
    public void testGenerateId() {
        // suck it :)
        Assert.assertFalse(new IdService().generateId().contains("-"));
    }
}
