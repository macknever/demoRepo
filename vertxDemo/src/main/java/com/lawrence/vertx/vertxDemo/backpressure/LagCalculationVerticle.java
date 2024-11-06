package com.lawrence.vertx.vertxDemo.backpressure;

import io.vertx.core.AbstractVerticle;

public class LagCalculationVerticle extends AbstractVerticle {

    private long totalBytesSent = 0;
    private long totalBytesReceived = 0;

    @Override
    public void start() {
        // Listen for sent data updates from the producer
        vertx.eventBus().consumer("producer.metrics.sent", message -> {
            totalBytesSent = (long) message.body();
            calculateAndLogLag();
        });

        // Listen for received data updates from the consumer
        vertx.eventBus().consumer("consumer.metrics.received", message -> {
            totalBytesReceived = (long) message.body();
            calculateAndLogLag();
        });
    }

    // Calculate lag based on current totalBytesSent and totalBytesReceived
    private void calculateAndLogLag() {
        long lag = totalBytesSent - totalBytesReceived;
        
        System.out.println(
                "Lag calculation: Total sent = " + totalBytesSent + ", Total received = " + totalBytesReceived +
                        ", Lag = " + lag + " bytes");
    }
}

