package com.lawrence.vertx.vertxDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class WorkerVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(WorkerVerticle.class);

    @Override
    public void start() {
        vertx.setPeriodic(10_000, id -> {
            try {
                logger.info("Zzz...");
                Thread.sleep(8000);
                logger.info("Up!");
            } catch (InterruptedException e) {
                logger.error("Woops", e);
            }
        });
    }

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        DeploymentOptions opts = new DeploymentOptions()
                .setInstances(2)
                .setWorker(false);
        vertx.deployVerticle(WorkerVerticle.class, opts);
    }
}
