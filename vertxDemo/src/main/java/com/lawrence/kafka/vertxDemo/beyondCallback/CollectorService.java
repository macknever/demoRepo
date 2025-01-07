package com.lawrence.kafka.vertxDemo.beyondCallback;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpResponseExpectation;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
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
        Future.all(
                        fetchTemperature(3000),
                        fetchTemperature(3001),
                        fetchTemperature(3002))
                .flatMap(this::sendToSnapshot)
                .onSuccess(data -> request.response()
                        .putHeader("Content-Type", "application/json")
                        .end(data.encode())

                )
                .onFailure(err -> {
                    LOG.error("Something went wrong, {}", err);
                    request.response().setStatusCode(500).end();
                });
    }

    private Future<JsonObject> sendToSnapshot(CompositeFuture temp) {
        List<JsonObject> tempData = temp.list();
        JsonObject jsonObject = new JsonObject()
                .put("data", new JsonArray()
                        .add(tempData.get(0))
                        .add(tempData.get(1))
                        .add(tempData.get(2)));
        return webClient
                .post(4000, "localhost", "/")
                .sendJson(jsonObject)
                .expecting(HttpResponseExpectation.SC_SUCCESS)
                .map(response -> jsonObject);
    }

    private void sendResponse(HttpServerRequest request, JsonObject data) {
        request.response()
                .putHeader("Content-Type", "application/json")
                .end(data.encode());
    }

    /**
     * Send a GET call to given port and after get response, return the {@link Future} of the body.
     *
     * @param port
     * @return
     */
    private Future<JsonObject> fetchTemperature(int port) {
        return webClient
                .get(port, "localhost", "/")
                .as(BodyCodec.jsonObject())
                .send()
                .expecting(HttpResponseExpectation.SC_SUCCESS)
                .map(HttpResponse::body);

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
