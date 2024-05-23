package com.lawrence.corejava.concurrency;

import java.util.Arrays;

public class Bank {
    private final double[] accounts;
    private final Sender sender;
    private final Receiver receiver;

    public Bank(final int n, final double initBalance) {
        this.sender = new Sender();
        this.receiver = new Receiver();
        this.accounts = new double[n];
        Arrays.fill(accounts, initBalance);
    }

    public Bank(final int n, final Sender sender, final Receiver receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.accounts = new double[n];
        double initBalance = 1000.0d;
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

    public void transferWithMonitor(int from, int to, double amount) {
        if (accounts[from] < amount) {
            return;
        }

        System.out.print(Thread.currentThread());
        synchronized (sender) {
            accounts[from] -= amount;
            System.out.printf(" %10.2f from %d to %d", amount, from, to);
        }

        //synchronized (receiver) {
        accounts[to] += amount;
        System.out.printf(" Total Balance: %10.2f%n", getTotalBalance());
        // }

    }
}
