package org.qinarmy.foundation.tx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class SimpleTests {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTests.class);

    @Test
    public void threadLocal() {
        ThreadLocal<Object> local = new ThreadLocal<>();
        local.set(null);
        System.out.println(local.get());
    }

    @Test
    public void now() {

    }


}
