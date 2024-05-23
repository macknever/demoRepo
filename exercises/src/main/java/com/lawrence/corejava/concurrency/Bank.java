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
        if (accounts[from] < amount) {
            return;
        }
        System.out.print(Thread.currentThread());

        accounts[from] -= amount;
        System.out.printf(" %10.2f from %d to %d", amount, from, to);

        accounts[to] += amount;
        System.out.printf(" Total Balance: %10.2f%n", getTotalBalance());
    }

    public double getTotalBalance() {
        double sum = 0.0d;

        for (double a : accounts) {
            sum += a;
        }

        return sum;
    }

    public int size() {
        return accounts.length;
    }
}
