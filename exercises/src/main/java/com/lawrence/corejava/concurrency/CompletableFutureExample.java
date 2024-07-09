package com.lawrence.corejava.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Show how {@link CompletableFuture} can be used
 * <li>Simple {@link Future}</li>
 */
public class CompletableFutureExample {

    /**
     * Example of how {@link CompletableFuture} and be used as a simple {@link Future}
     * Use an {@link Executors} to complete the completable future in 500 ms
     *
     * @param simpleString The String needs to be put in the completable future.
     * @return simple {@link Future}
     */
    public Future<String> simpleFuture(final String simpleString) {
        CompletableFuture<String> stringFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            Thread.sleep(500L);
            stringFuture.complete(simpleString);
            return null;
        });

        return stringFuture;
    }

    public Future<String> staticFuture(final String simpleString) {
        return CompletableFuture.completedFuture(simpleString);
    }

    public CompletableFuture<Void> chainedComputation(Supplier<String> initSupplier,
            Consumer<String> secondConsumer, Runnable runnable) {
        return CompletableFuture
                .supplyAsync(initSupplier)
                .thenAccept(secondConsumer)
                .thenRun(runnable);
    }

    public CompletableFuture<String> combinedFuture(final String str1, final String str2) {
        return CompletableFuture.supplyAsync(() -> str1).thenCompose(s -> CompletableFuture.supplyAsync(() -> s + str2));
    }

}
