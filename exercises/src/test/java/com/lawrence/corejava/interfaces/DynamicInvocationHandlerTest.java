package com.lawrence.corejava.interfaces;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.junit.jupiter.api.Test;

class DynamicInvocationHandlerTest {

    @Test
    void proxyTest() {
        Map proxyInstance = (Map) Proxy.newProxyInstance(
                DynamicInvocationHandlerTest.class.getClassLoader(),
                new Class[]{ Map.class },
                new DynamicInvocationHandler());

        assertThat((int) proxyInstance.get("hello")).describedAs("get call")
                .isEqualTo(42);

        proxyInstance.put("hello", "world");
        // assertThrows(UnsupportedOperationException.class, () -> proxyInstance.put("hello", "world"));

    }

}
