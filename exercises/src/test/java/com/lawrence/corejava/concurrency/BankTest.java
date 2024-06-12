package com.lawrence.corejava.concurrency;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class BankTest {
    private static final boolean DEPOSIT = true;
    private static final boolean WITHDRAW = false;
    private static final boolean SAFE = true;
    private static final boolean UNSAFE = false;

    @Test
    void testSynchronization() throws InterruptedException {
        Bank bank = new Bank(10000.0);
        Account account = new Account(10000.0);

        final int safeDepositCount = 5;
        final int unsafeDepositCount = 5;
        final int safeWithdrawCount = 5;
        final int unsafeWithdrawCount = 5;
        Thread[] safeThreads = new Thread[safeDepositCount + safeWithdrawCount];
        Thread[] unsafeThreads = new Thread[unsafeDepositCount + unsafeWithdrawCount];


        for (int i = 0; i < safeDepositCount; i++) {
            safeThreads[i] = new Thread(new Agent(bank, account, DEPOSIT, SAFE));
        }

        for (int i = safeDepositCount; i < safeDepositCount + safeWithdrawCount; i++) {
            safeThreads[i] = new Thread(new Agent(bank, account, WITHDRAW, SAFE));
        }

        for (int i = 0; i < unsafeDepositCount; i++) {
            unsafeThreads[i] = new Thread(new Agent(bank, account, DEPOSIT, UNSAFE));
        }

        for (int i = unsafeDepositCount; i < unsafeDepositCount + unsafeWithdrawCount; i++) {
            unsafeThreads[i] = new Thread(new Agent(bank, account, WITHDRAW, UNSAFE));
        }


        // Run unsafe operations
        for (Thread t : unsafeThreads) {
            t.start();
        }

        for (Thread t : unsafeThreads) {
            t.join();
        }

        System.out.printf("Final vault balance (unsafe): %10.2f%n", bank.getVault());
        System.out.printf("Final account balance (unsafe): %10.2f%n", account.getBalance());

        // Reset bank and account
        bank = new Bank(10000.0);
        account = new Account(1000.0);

        // Run safe operations
        for (Thread t : safeThreads) {
            t.start();
        }

        for (Thread t : safeThreads) {
            t.join();
        }

        System.out.printf("Final vault balance (safe): %10.2f%n", bank.getVault());
        System.out.printf("Final account balance (safe): %10.2f%n", account.getBalance());
    }

    @Test
    void transferWithExecutor() {
        Bank bank = new Bank(10000.0);
        Account account = new Account(0);

        List<Agent> unsafeAgents = new ArrayList<>();
        unsafeAgents.add(new Agent(bank, account, DEPOSIT, UNSAFE));
        unsafeAgents.add(new Agent(bank, account, DEPOSIT, UNSAFE));

        BankService unsafeService = new BankService();

        unsafeService.transferService(unsafeAgents);

        System.out.printf("Final vault balance (unsafe): %10.2f%n", bank.getVault());
        System.out.printf("Final account balance (unsafe): %10.2f%n", account.getBalance());

        bank = new Bank(10000.0);
        account = new Account(0);

        List<Agent> safeAgents = new ArrayList<>();
        safeAgents.add(new Agent(bank, account, DEPOSIT, SAFE));
        safeAgents.add(new Agent(bank, account, DEPOSIT, SAFE));

        BankService safeService = new BankService();

        safeService.transferService(safeAgents);

        System.out.printf("Final vault balance (safe): %10.2f%n", bank.getVault());
        System.out.printf("Final account balance (safe): %10.2f%n", account.getBalance());

    }

}
