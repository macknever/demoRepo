package com.lawrence.corejava.concurrency;

import java.util.concurrent.RecursiveTask;
import java.util.function.DoublePredicate;

public class LargeCounter extends RecursiveTask<Integer> {

    private static final int THRESHOLD = 10000;
    private double[] values;
    private int from;
    private int to;
    private DoublePredicate filter;

    public LargeCounter(double[] values, int from, int to, DoublePredicate filter) {
        this.values = values;
        this.from = from;
        this.to = to;
        this.filter = filter;
    }

    public Integer compute() {

        if (to - from < THRESHOLD) {
            int count = 0;
            for (int i = from; i < to; i++) {
                if (filter.test(values[i])) {
                    count++;
                }
            }
            return count;
        } else {
            int mid = from + (to - from) / 2;
            LargeCounter first = new LargeCounter(values, from, mid, filter);
            LargeCounter second = new LargeCounter(values, mid, to, filter);
            invokeAll(first, second);
            return first.join() + second.join();
        }
    }
}
