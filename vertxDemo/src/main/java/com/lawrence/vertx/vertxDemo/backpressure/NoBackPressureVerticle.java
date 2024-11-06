package com.lawrence.vertx.vertxDemo.backpressure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.client.WebClient;

public class NoBackPressureVerticle extends AbstractVerticle {

    private long totalBytesReceived = 0;
    private long totalBytesSent = 0;

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(req -> {
            req.handler(buffer -> {
                // Simulate slow processing by sleeping
                try {
                    Thread.sleep(100);  // Slows down the consumer to simulate an overwhelmed state
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Track the total number of bytes received
                totalBytesReceived += buffer.length();
                long lag = totalBytesSent - totalBytesReceived;
                System.out.println("Total bytes received: " + totalBytesReceived + ", Lag: " + lag + " bytes");
            });

            req.endHandler(v -> {
                req.response().end("Finished processing");
            });
        });

        server.listen(8080, res -> {
            if (res.succeeded()) {
                System.out.println("Server started on port 8080");
            } else {
                System.out.println("Failed to start server");
            }
        });

        // Simulate a fast producer using WebClient
        WebClient client = WebClient.create(vertx);

        vertx.setTimer(500, id -> {
            for (int i = 0; i < 1000; i++) {
                // Sending large amounts of data quickly
                client.post(8080, "localhost", "/")
                        .sendBuffer(Buffer.buffer(new byte[100000]), ar -> {
                            if (ar.succeeded()) {
                                totalBytesSent += 100000;
                                System.out.println("Client sent: " + totalBytesSent + " bytes");
                            } else {
                                ar.cause().printStackTrace();
                            }
                        });
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new NoBackPressureVerticle());
    }
}

