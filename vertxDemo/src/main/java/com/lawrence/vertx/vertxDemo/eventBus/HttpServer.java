package com.lawrence.vertx.vertxDemo.eventBus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.TimeoutStream;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

/**
 * An HTTP server to expose data to web
 */
public class HttpServer extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(this::handler)
                .listen(config().getInteger("port", 8080));
    }

    /**
     * / index.html to show data
     * /sse to process data for default address
     *
     * @param request
     */
    private void handler(HttpServerRequest request) {
        if ("/".equals(request.path())) {
            request.response().sendFile("index.html");
        } else if ("/sse".equals(request.path())) {
            sse(request);
        } else {
            request.response().setStatusCode(404);
        }
    }

    /**
     * @param request
     */
    private void sse(HttpServerRequest request) {
        HttpServerResponse response = request.response();
        response
                .putHeader("Content-Type", "text/event-stream")
                .putHeader("Cache-Control", "no-cache")
                .setChunked(true);
        // this consumer will consumer data from "sensor.updates" topic and show them onto /sse
        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("sensor.updates");
        consumer.handler(msg -> {
            response.write("event: update\n");
            response.write("data: " + msg.body().encode() + "\n\n");
        });

        // A periodic handler
        // send an empty message to "sensor.average"
        // Another consumer in {@link SensorData} will reply computed average data
        // This handler will write the replied data to the response.
        TimeoutStream ticks = vertx.periodicStream(1000);
        ticks.handler(id -> {
            vertx.eventBus().<JsonObject>request("sensor.average", "",
                    reply -> {
                        if (reply.succeeded()) {
                            response.write("event: average\n");
                            response.write("data: " + reply.result().body().encode() + "\n\n");
                        }
                    });
        });
        response.endHandler(v -> {
            consumer.unregister();
            ticks.cancel();
        });
    }
}

