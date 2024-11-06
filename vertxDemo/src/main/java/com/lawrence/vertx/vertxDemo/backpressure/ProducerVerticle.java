package com.lawrence.vertx.vertxDemo.backpressure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;

public class ProducerVerticle extends AbstractVerticle {

    private long totalBytesSent = 0;

    @Override
    public void start() {
        WebClient client = WebClient.create(vertx);

        vertx.setPeriodic(100, id -> {
            totalBytesSent += 100000;
            vertx.eventBus().publish("producer.metrics.sent", totalBytesSent);

            // Simulate sending large amounts of data quickly
            client.post(8080, "localhost", "/")

                    .sendBuffer(Buffer.buffer(new byte[100000]), ar -> {

                        if (ar.succeeded()) {
                            System.out.println("Producer sent: " + totalBytesSent + " bytes");

                            // Publish totalBytesSent to the Event Bus
                        } else {
                            ar.cause().printStackTrace();
                        }
                    });
        });
    }
}


