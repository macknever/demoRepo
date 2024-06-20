package com.lawrence.corejava.concurrency;

import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;

class LargeCounterTest {

    @Test
    void forkJoinPoolShouldWork() {
        final int size = 10000000;
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = Math.random();
        }

        LargeCounter counter = new LargeCounter(values, 0, size, x -> x > 0.5);
        var pool = new ForkJoinPool();
        pool.invoke(counter);
        System.out.println(counter.join());
    }

}
