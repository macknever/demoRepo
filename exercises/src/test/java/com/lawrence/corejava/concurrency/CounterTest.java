package com.lawrence.corejava.concurrency;

import org.junit.jupiter.api.Test;

public class CounterTest {

    private static final int NUM_THREADS = 100;
    private static final int INCREMENTS_PER_THREAD = 1000;

    @Test
    void testWithoutSynchronization() throws InterruptedException {
        Counter counter = new Counter();
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.printf("Final count without synchronization: %d%n", counter.getCount());
    }

    @Test
    void testWithSynchronization() throws InterruptedException {
        Counter counter = new Counter();
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS_PER_THREAD; j++) {
                    counter.incrementWithSync();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.printf("Final count with synchronization: %d%n", counter.getCount());
    }
}
