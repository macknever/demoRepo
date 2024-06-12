package com.lawrence.corejava.concurrency;

public class Agent implements Runnable {
    private final Bank bank;
    private final Account account;
    private final boolean isDeposit; // true for deposit, false for withdraw
    private final boolean safe; // true for thread-safe operations, false for unsafe

    public Agent(Bank bank, Account account, boolean isDeposit, boolean safe) {
        this.bank = bank;
        this.account = account;
        this.isDeposit = isDeposit;
        this.safe = safe;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            double amount = 1d;
            if (isDeposit) {
                if (safe) {
                    bank.transferToAccountSafe(account, amount);
                } else {
                    bank.transferToAccountUnsafe(account, amount);
                }
            } else {
                if (safe) {
                    bank.transferFromAccountSafe(account, amount);
                } else {
                    bank.transferFromAccountUnsafe(account, amount);
                }
            }
        }
    }
}

