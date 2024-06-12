package com.lawrence.corejava.concurrency;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BankService {
    private final ExecutorService service = Executors.newFixedThreadPool(2);
    private static final int TIME_OUT_MIN = 10;

    public void transferService(List<Agent> agents) {

        for (var agent : agents) {
            service.submit(agent);
        }

        service.shutdown();

        try {
            service.awaitTermination(TIME_OUT_MIN, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
