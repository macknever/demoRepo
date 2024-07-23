package com.lawrence.corejava.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
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

        assertThat(future.get()).isEqualTo(expected);
    }

    @Test
    void staticFutureTest() throws Exception {
        String expected = "expected String";

        Future<String> future = example.staticFuture(expected);

        assertThat(future.get()).isEqualTo(expected);
    }

    @Test
    void chainComputationTest() {
        final String initStr = RandomStringUtils.random(4, true, false);
        System.out.printf("init String: %s \n", initStr);
        example.chainedComputation(() -> initStr,
                s -> System.out.printf("This is what has been initialized: %s\n", s),
                () -> System.out.println("chained computation completed"));
    }

    @Test
    void composedFutureTest() throws ExecutionException, InterruptedException {
        final String str1 = RandomStringUtils.random(4, true, true);
        final String str2 = RandomStringUtils.random(4, true, true);

        assertThat(str1 + str2).isEqualTo(example.composedFuture(str1, str2).get());

    }

    @Test
    void combinedFutureTest() throws ExecutionException, InterruptedException {
        final int num1 = new Random().nextInt(99);
        final int num2 = new Random().nextInt(99);

        assertThat(num1 * num2).isEqualTo(example.combinedFuture(num1, num2).get());

    }

}
