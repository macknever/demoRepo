package com.lawrence.kafka.vertxDemo.beyondCallback;

import java.util.Random;
import java.util.UUID;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

public class HeatSensor extends AbstractVerticle {
    private final Random random = new Random();
    private final String sensorId = UUID.randomUUID().toString();
    private double temperature = 21.0;

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(this::handleRequest)
                .listen(config().getInteger("http.port", 3000));

        scheduleNextUpdate();
    }

    private void scheduleNextUpdate() {
        vertx.setTimer(random.nextInt(5000) + 1000, this::update);
    }

    private void update(long timerId) {
        temperature = temperature + (delta() / 10);
        scheduleNextUpdate();
    }

    /**
     * @return Gaussian distributed double, mean 0.0 standard deviation 1.0
     */
    private double delta() {
        if (random.nextInt() > 0) {
            return random.nextGaussian();
        } else {
            return -random.nextGaussian();
        }
    }

    private void handleRequest(HttpServerRequest req) {
        JsonObject jsonObject = new JsonObject()
                .put("id", sensorId)
                .put("temp", temperature);

        req.response()
                .putHeader("Content-Type", "application/json")
                .end(jsonObject.encode());

    }
}
