package com.lawrence.pizza.verticles;

import static com.lawrence.pizza.infrastructure.Addresses.KITCHEN_SERVICE;
import static com.lawrence.pizza.infrastructure.Addresses.STATUS_INTERNAL;
import static com.lawrence.pizza.models.OrderStatus.COOKING;
import static com.lawrence.pizza.models.OrderStatus.READY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.lawrence.pizza.models.events.StatusUpdate;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class KitchenVerticleTest {

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new KitchenVerticle())
                .onComplete(testContext.succeedingThenComplete());
    }

    @Test
    void orderPlaced_cookPizza_updateStatus(Vertx vertx, VertxTestContext ctx) throws VertxException {
        Checkpoint checkpoint = ctx.checkpoint(2);
        AtomicInteger updateCount = new AtomicInteger(0);


        vertx.eventBus().<JsonObject>consumer(STATUS_INTERNAL, msg -> {

        });

        // verify logic
        vertx.eventBus().send(KITCHEN_SERVICE, sampleOrderRequest());

    }

    private static JsonObject nonOrderRequest() {
        return new JsonObject()
                .put("clientId", "c-1");
    }

    private static JsonObject sampleOrderRequest() {
        return new JsonObject()
                .put("customerId", "c-1")
                .put("orderId", UUID.randomUUID().toString())
                .put("status", "PLACED")
                .put("items", new JsonArray()
                        .add(new JsonObject()
                                .put("productId", "p-1")
                                .put("name", "Pepperoni")
                                .put("size", "LARGE")
                                .put("quantity", 1)
                                .put("price", 18.99)))
                .put("totalAmount", 18.99);
    }
}