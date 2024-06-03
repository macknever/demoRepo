package com.lawrence.corejava.concurrency;

public class Bank {
    private double vault;
    private final Object backManager = new Object();

    public Bank(double initialVault) {
        this.vault = initialVault;
    }

    public void transferToAccountUnsafe(Account account, double amount) {
        if (vault >= amount) {
            vault -= amount;
            account.deposit(amount);
        }
    }

    public void transferFromAccountUnsafe(Account account, double amount) {
        if (account.withdraw(amount)) {
            vault += amount;
        }
    }

    public void transferToAccountSafe(Account account, double amount) {
        synchronized (backManager) {
            if (vault >= amount) {
                vault -= amount;
                account.deposit(amount);
            }
        }
    }

    public void transferFromAccountSafe(Account account, double amount) {
        synchronized (backManager) {
            if (account.withdraw(amount)) {
                vault += amount;
            }
        }
    }

    public double getVault() {
        return vault;
    }
}
