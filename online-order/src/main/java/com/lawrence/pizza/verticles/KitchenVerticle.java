package com.lawrence.pizza.verticles;

import static com.lawrence.pizza.infrastructure.Addresses.KITCHEN_SERVICE;
import static com.lawrence.pizza.infrastructure.Addresses.STATUS_INTERNAL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawrence.pizza.models.Order;
import com.lawrence.pizza.models.OrderStatus;
import com.lawrence.pizza.models.events.StatusUpdate;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class KitchenVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(KitchenVerticle.class);
    private static final long EST_COOKING_TIME = 3000L;

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(KITCHEN_SERVICE, this::cookPizza);
    }

    private void cookPizza(Message<JsonObject> msg) {
        final Order order;
        try {
            order = msg.body().mapTo(Order.class);
        } catch (Exception e) {
            LOG.error("Error parsing order message", e);
            msg.fail(400, "Error parsing order message");
            return;
        }

        long cookMS = config().getLong("kitchen.cooking.time", EST_COOKING_TIME);
        LOG.info("[Kitchen] {} has been received", order.getOrderId());

        updateStatus(order.getOrderId(), order.getStatus(), OrderStatus.COOKING, cookMS);

        vertx.setTimer(cookMS, id -> {
            LOG.info("[Kitchen] Pizza for {} is ready", order.getOrderId());
            updateStatus(order.getOrderId(), OrderStatus.COOKING, OrderStatus.READY, cookMS);
        });
    }

    private void updateStatus(String orderId, OrderStatus oldS, OrderStatus newS, long estMs) {
        StatusUpdate update = new StatusUpdate(
                orderId,
                oldS,
                newS,
                System.currentTimeMillis(),
                estMs > 0 ? System.currentTimeMillis() + estMs : 0
        );

        vertx.eventBus().send(STATUS_INTERNAL, JsonObject.mapFrom(update));
    }
}
