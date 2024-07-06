package com.lawrence.corejava.concurrency;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompletableFutureExampleTest {

    private CompletableFutureExample example;

    @BeforeEach
    void setup() {
        example = new CompletableFutureExample();
    }

    @Test
    void simpleFutureTest() throws Exception {
        String expected = "expected String";

        Future<String> future = example.simpleFuture(expected);
        Awaitility.await().atMost(600, TimeUnit.MILLISECONDS).until(() -> future.isDone());

        Assertions.assertThat(future.get()).isEqualTo(expected);
    }

    @Test
    void staticFutureTest() throws Exception {
        String expected = "expected String";

        Future<String> future = example.staticFuture(expected);

        Assertions.assertThat(future.get()).isEqualTo(expected);
    }

    @Test
    void chainComputationTest() {
        final String initStr = RandomStringUtils.random(4, true, false);
        System.out.printf("init String: %s \n", initStr);
        example.chainedComputation(() -> initStr,
                s -> System.out.printf("This is what has been initialized: %s\n", s),
                () -> System.out.println("chained computation completed"));
    }

}
