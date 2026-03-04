package com.lawrence.pizza.verticles;

import static com.lawrence.pizza.infrastructure.Addresses.ORDER_ORCHESTRATOR;

import java.util.UUID;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawrence.pizza.OrderException;
import com.lawrence.pizza.models.Order;
import com.lawrence.pizza.models.OrderStatus;

public class GatewayVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(GatewayVerticle.class);
    private static final long EVENT_BUS_TIMEOUT = 2000L;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());

        router.post("/order").respond(ctx -> placeOrder(ctx).map(JsonObject::mapFrom));

        HttpServer server = vertx.createHttpServer();

        final int port = config().getInteger("http.port", 8181);

        server.requestHandler(router)
                .listen(port)
                .onSuccess(serverResponse -> {
                    LOG.info("Server listening on port {}", port);
                    startPromise.complete();
                })
                .onFailure(serverResponse -> {
                    LOG.error("Server listening on port {} failed", port, serverResponse);
                    startPromise.fail(serverResponse);
                });
    }

    private Future<Order> placeOrder(RoutingContext ctx) {
        JsonObject json = ctx.body().asJsonObject();
        if (json == null) {
            return Future.failedFuture("Invalid request: body must be JSON");
        }

        final Order order;

        try {
            order = json.mapTo(Order.class);
        } catch (Exception e) {
            return Future.failedFuture(new OrderException("Request body is not an Order"));
        }

        order.setOrderId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.PLACED);

        var opts = new DeliveryOptions().setSendTimeout(EVENT_BUS_TIMEOUT);

        return vertx.eventBus()
                .<JsonObject>request(ORDER_ORCHESTRATOR, JsonObject.mapFrom(order), opts)
                .compose(reply -> {
                    Boolean accepted = reply.body().getBoolean("accepted");
                    if (accepted == null) {
                        return Future.failedFuture("Order was not accepted");
                    }
                    if (!accepted) {
                        order.setStatus(OrderStatus.FAILED);
                    }
                    return Future.succeededFuture(order);
                })
                .onFailure(e -> LOG.error("Place order failed", e));

    }
}
