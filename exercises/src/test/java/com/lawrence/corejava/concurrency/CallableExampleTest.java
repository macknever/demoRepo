package com.lawrence.corejava.concurrency;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

class CallableExampleTest {

    @Test
    void callableShouldWork() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CallableExample callableExample = new CallableExample();

        Future<Integer> future = executorService.submit(callableExample);

        System.out.println(future.get());
    }

}
