package com.lawrence.corejava.concurrency;

import org.junit.jupiter.api.Test;

class BankTest {
    public static final int ACCOUNTS_NUM = 1000;
    public static final double INIT_BALANCE = 1000d;
    public static final double MAC_AMOUNT = 1000d;
    public static final int DELAY = 10;

    @Test
    void unSynBankTest() {
        Bank bank = new Bank(ACCOUNTS_NUM, INIT_BALANCE);

        for (int i = 0; i < ACCOUNTS_NUM; i++) {
            int fromAccount = i;
            Runnable r = () -> {
                try {
                    while (true) {
                        int toAccount = (int) (bank.size() * Math.random());
                        double amount = MAC_AMOUNT * Math.random();
                        bank.transfer(fromAccount, toAccount, amount);
                        Thread.sleep((int) (DELAY * Math.random()));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            var t = new Thread(r);
            t.start();
        }
    }

    @Test
    void synBankTest() {
        Bank bank = new Bank(ACCOUNTS_NUM, INIT_BALANCE);

        for (int i = 0; i < ACCOUNTS_NUM; i++) {
            int fromAccount = i;
            Runnable r = () -> {
                try {
                    while (true) {
                        int toAccount = (int) (bank.size() * Math.random());
                        double amount = MAC_AMOUNT * Math.random();
                        bank.transferWithMonitor(fromAccount, toAccount, amount);
                        Thread.sleep((int) (DELAY * Math.random()));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            var t = new Thread(r);
            t.start();
        }
    }

    @Test
    void unSynBankTest2() {
        Sender sender = new Sender();
        Receiver receiver = new Receiver();
        Bank bank = new Bank(ACCOUNTS_NUM, sender, receiver);

        for (int i = 0; i < ACCOUNTS_NUM; i++) {
            int fromAccount = i;
            Runnable r = () -> {
                try {
                    while (true) {
                        int toAccount = (int) (bank.size() * Math.random());
                        double amount = MAC_AMOUNT * Math.random();
                        bank.transferWithMonitor(fromAccount, toAccount, amount);
                        Thread.sleep((int) (DELAY * Math.random()));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            var t = new Thread(r);
            t.start();
        }
    }

}
