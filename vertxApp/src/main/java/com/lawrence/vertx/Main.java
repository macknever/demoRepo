package com.lawrence.vertx;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        // Create a Vertx instance
        Vertx vertx = Vertx.vertx();

        // Deploy the KafkaWebAppVerticle
        vertx.deployVerticle(new KafkaWebAppVerticle(), res -> {
            if (res.succeeded()) {
                System.out.println("KafkaWebAppVerticle deployed successfully.");
            } else {
                System.err.println("Failed to deploy KafkaWebAppVerticle: " + res.cause());
            }
        });
    }
}
