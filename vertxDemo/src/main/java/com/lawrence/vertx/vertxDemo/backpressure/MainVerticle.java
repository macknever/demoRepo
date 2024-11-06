package com.lawrence.vertx.vertxDemo.backpressure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        DeploymentOptions opts = new DeploymentOptions()
                .setWorker(false);

        // Deploy the producer and consumer verticles
        vertx.deployVerticle(new ProducerVerticle(), opts);
        vertx.deployVerticle(new ConsumerVerticle(), opts);
        vertx.deployVerticle(new LagCalculationVerticle(), opts);
    }
}

