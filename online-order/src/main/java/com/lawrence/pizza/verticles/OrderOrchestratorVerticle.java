package com.lawrence.pizza.verticles;

import static com.lawrence.pizza.infrastructure.Addresses.KITCHEN_SERVICE;
import static com.lawrence.pizza.infrastructure.Addresses.ORDER_ORCHESTRATOR;
import static com.lawrence.pizza.infrastructure.Addresses.ORDER_STATUS_UPDATES;
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
import io.vertx.core.shareddata.LocalMap;

public class OrderOrchestratorVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(OrderOrchestratorVerticle.class);

    private LocalMap<String, JsonObject> orderStore;

    @Override
    public void start() {
        orderStore = vertx.sharedData().getLocalMap("order.db");
        vertx.eventBus().consumer(ORDER_ORCHESTRATOR, this::processNewOrder);
        vertx.eventBus().consumer(STATUS_INTERNAL, this::processStatusUpdate);
    }

    private void processNewOrder(Message<JsonObject> msg) {


        Future.succeededFuture()
                .map(v -> {
                    Order order = msg.body().mapTo(Order.class);
                    order.setStatus(OrderStatus.CONFIRMED);
                    orderStore.put(order.getOrderId(), JsonObject.mapFrom(order));
                    return order;
                })
                .onSuccess(order -> {
                    msg.reply(new JsonObject().put("accepted", true));
                    broadcastStatus(order.getOrderId(), OrderStatus.PLACED, OrderStatus.CONFIRMED);
                    vertx.eventBus().send(KITCHEN_SERVICE, JsonObject.mapFrom(order));
                })
                .onFailure(err -> {
                    msg.fail(500, err.getMessage());
                });
    }

    private void processStatusUpdate(Message<JsonObject> msg) {
        Future.succeededFuture()
                .map(v -> {
                  StatusUpdate update = msg.body().mapTo(StatusUpdate.class);
                  JsonObject currentOrderJson = orderStore.get(update.orderId());
                  Order currentOrder = currentOrderJson.mapTo(Order.class);
                  if (currentOrder != null) {
                      currentOrder.setStatus(update.newStatus());
                      orderStore.put(update.orderId(), JsonObject.mapFrom(currentOrder));
                  }
                  return update;
                })
                .onSuccess(update -> {
                    LOG.info("Order updated successfully");
                    vertx.eventBus().publish(ORDER_STATUS_UPDATES, JsonObject.mapFrom(update));
                })
                .onFailure(err -> {
                    msg.fail(500, err.getMessage());
                });
    }

    private void broadcastStatus(String id, OrderStatus oldS, OrderStatus newS) {
        StatusUpdate update = new StatusUpdate(id, oldS, newS, System.currentTimeMillis(), 0);
        vertx.eventBus().publish(ORDER_STATUS_UPDATES, JsonObject.mapFrom(update));
    }

}
