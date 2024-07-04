package com.lawrence.corejava.concurrency;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class CompletableFutureExampleTest {

    @Test
    void simpleFutureTest() throws Exception {
        CompletableFutureExample example = new CompletableFutureExample();
        String expected = "expected String";

        Future<String> future = example.simpleFuture(expected);
        Awaitility.await().atMost(600, TimeUnit.MILLISECONDS).until(() -> future.isDone());

        Assertions.assertThat(future.get()).isEqualTo(expected);
    }

    @Test
    void staticFutureTest() throws Exception {
        CompletableFutureExample example = new CompletableFutureExample();
        String expected = "expected String";

        Future<String> future = example.staticFuture(expected);

        Assertions.assertThat(future.get()).isEqualTo(expected);
    }

}
