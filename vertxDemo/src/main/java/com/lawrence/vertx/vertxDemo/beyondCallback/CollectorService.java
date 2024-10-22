package com.lawrence.vertx.vertxDemo.beyondCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

public class CollectorService extends AbstractVerticle {
    public static final Logger LOG = LoggerFactory.getLogger(CollectorService.class);
    private WebClient webClient;

    @Override
    public void start(Promise<Void> promise) {
        webClient = WebClient.create(vertx);
        vertx.createHttpServer()
                .requestHandler(this::handleRequest)
                .listen(8080)
                .onFailure(promise::fail)
                .onSuccess(ok -> {
                    System.out.println("https://localhost:8080");
                    promise.complete();
                });
    }

    private void handleRequest(HttpServerRequest request) {
        List<JsonObject> responses = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 3; i++) {
            webClient.get(3000 + i, "localhost", "/")
                    .expect(ResponsePredicate.SC_SUCCESS)
                    .as(BodyCodec.jsonObject())
                    .send(ar -> {
                        if (ar.succeeded()) {
                            responses.add(ar.result().body());
                        } else {
                            LOG.error("Sensor down?", ar.cause());
                        }

                        if (counter.incrementAndGet() == 3) {
                            JsonObject data = new JsonObject()
                                    .put("data", new JsonArray(responses));
                            sendToSnapshot(request, data);
                        }
                    });
        }
    }

    private void sendToSnapshot(HttpServerRequest request, JsonObject data) {
        webClient.post(4000, "localhost", "/")
                .expect(ResponsePredicate.SC_SUCCESS)
                .sendJsonObject(data, ar -> {
                    if (ar.succeeded()) {
                        sendResponse(request, data);
                    } else {
                        LOG.error("Snapshot down?", ar.cause());
                        request.response().setStatusCode(500).end();
                    }

                });
    }

    private void sendResponse(HttpServerRequest request, JsonObject data) {
        request.response()
                .putHeader("Content-Type", "application/json")
                .end(data.encode());
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(HeatSensor.class,
                new DeploymentOptions().setConfig(new JsonObject().put("http.port", 3000)));
        vertx.deployVerticle(HeatSensor.class,
                new DeploymentOptions().setConfig(new JsonObject().put("http.port", 3001)));
        vertx.deployVerticle(HeatSensor.class,
                new DeploymentOptions().setConfig(new JsonObject().put("http.port", 3002)));

        vertx.deployVerticle("com.lawrence.vertx.vertxDemo.beyondCallback.SnapshotService");
        vertx.deployVerticle("com.lawrence.vertx.vertxDemo.beyondCallback.CollectorService");
    }
}
