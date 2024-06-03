package com.lawrence.corejava.concurrency;

public class Counter {
    private int count = 0;
    private final Object lock = new Object();

    // Increment method without synchronization
    public void increment() {
        count++;
    }

    // Increment method with synchronization
    public void incrementWithSync() {
        synchronized (lock) {
            count++;
        }
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) {
        // Test code can go here
    }
}
