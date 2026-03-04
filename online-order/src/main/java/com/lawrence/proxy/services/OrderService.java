package com.lawrence.proxy.services;

import com.lawrence.pizza.models.Order;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface OrderService {
    String ADDRESS = "service.order.orchestrator";

    static OrderService createProxy(Vertx vertx) {
        return new OrderServiceVertxEBProxy(vertx, ADDRESS);
    }

    // Command: "Process this order and tell me if you accepted it"
    Future<JsonObject> placeOrder(Order order);
}
