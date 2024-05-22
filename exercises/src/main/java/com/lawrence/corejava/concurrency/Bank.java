package com.lawrence.corejava.concurrency;

import java.util.Arrays;

public class Bank {
    private final double[] accounts;

    public Bank(final int n, final double initBalance) {
        this.accounts = new double[n];
        Arrays.fill(accounts, initBalance);
    }

    public Bank(final int n) {
        this.accounts = new double[n];
        double initBalance = 0.0d;
        Arrays.fill(accounts, initBalance);
    }

    public void transfer(int from, int to, double amount) {
    }

    public void getTotalBalance() {
    }
}
