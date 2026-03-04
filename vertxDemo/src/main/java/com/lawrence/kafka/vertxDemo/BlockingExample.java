package com.lawrence.kafka.vertxDemo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockingExample extends AbstractVerticle {
    @Override
    public void start() {
        log.info("main thread Id: {}", Thread.currentThread());

        // awaits on an event loop
        vertx.executeBlocking(() -> {
            log.info("executing blocking thread Id: {}", Thread.currentThread());
            return returnNumNestedVertx();
        });
    }

    private Future<Integer> returnNumNestedVertx() throws Exception {
        Promise<Integer> promise = Promise.promise();
        log.info("returnNum thread Id: {}", Thread.currentThread());
        // it pushes the callback to an event loop because the current context is on an event loop

        vertx.setPeriodic(100L, id -> promise.complete(1));

        // awaits in a worker thread
        return promise.future();
    }

    private int returnNumWithJoin() throws Exception {
        Promise<Integer> promise = Promise.promise();

        log.info("returnNum thread Id: {}", Thread.currentThread());
        // it pushes the callback to an event loop because the current context is on an event loop

        promise.complete(1);
        // awaits in a worker thread
        return promise.future().toCompletionStage().toCompletableFuture().join();
    }

    private Future<Integer> returnNumWithoutJoin() throws Exception {
        Promise<Integer> promise = Promise.promise();
        log.info("returnNum thread Id: {}", Thread.currentThread());
        // it pushes the callback to an event loop because the current context is on an event loop

        promise.complete(1);
        // awaits in a worker thread
        return promise.future();
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(BlockingExample.class.getName(), res -> {
            if (res.succeeded()) {
                log.info("succeeded");
            }
            if (res.failed()) {
                log.info("failed");
            }
        });

    }
}
