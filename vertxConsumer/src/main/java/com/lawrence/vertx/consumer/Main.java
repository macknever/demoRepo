package com.lawrence.vertx.consumer;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new KafkaConsumerVerticle(), res -> {
            if (res.succeeded()) {
                System.out.println("KafkaConsumerVerticle deployed successfully.");
            } else {
                System.err.println("Failed to deploy KafkaConsumerVerticle: " + res.cause());
            }
        });

                vertx.deployVerticle(new CassVerticle(), res -> {
                    if (res.succeeded()) {
                        System.out.println("CassVerticle deployed successfully.");
                    } else {
                        System.err.println("Failed to deploy CassVerticle: " + res.cause());
                    }
                });
    }
}
