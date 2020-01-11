package org.qinarmy.foundation.tx;

import org.testng.annotations.Test;

public class SimpleTests {

    @Test
    public void threadLocal() {
        ThreadLocal<Object> local = new ThreadLocal<>();
        local.set(null);
        System.out.println(local.get());
    }

}
