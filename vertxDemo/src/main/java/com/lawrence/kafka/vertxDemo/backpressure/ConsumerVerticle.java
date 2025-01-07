package com.lawrence.kafka.vertxDemo.backpressure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

public class ConsumerVerticle extends AbstractVerticle {

    private long totalBytesReceived = 0;

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(req -> {
            req.pause();  // Pause incoming data

            req.handler(buffer -> {
                // Simulate slow processing
                try {
                    Thread.sleep(150);  // Simulate a slower consumer
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Track the total number of bytes received
                totalBytesReceived += buffer.length();
                System.out.println("Consumer received: " + totalBytesReceived + " bytes");

                // Publish totalBytesReceived to the Event Bus
                vertx.eventBus().publish("consumer.metrics.received", totalBytesReceived);

                // Resume data stream after processing
                req.resume();
            });

            req.endHandler(v -> {
                req.response().end("Processing complete");
            });

            vertx.setTimer(500, id -> req.resume());
        });

        server.listen(8080);
    }
}


