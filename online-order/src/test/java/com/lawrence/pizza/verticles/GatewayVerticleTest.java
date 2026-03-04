package com.lawrence.pizza.verticles;

import static com.lawrence.pizza.infrastructure.Addresses.ORDER_ORCHESTRATOR;
import static com.lawrence.pizza.infrastructure.Addresses.ORDER_STATUS_UPDATES;
import static com.lawrence.pizza.models.OrderStatus.FAILED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.registerCustomDateFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.lawrence.pizza.OrderException;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class GatewayVerticleTest {

    private int port;
    private static final String HOST = "localhost";
    private static final String ORDER_PATH = "/order";
    DeploymentOptions opts;
    WebClient webClient;

    @BeforeEach
    void setUp(Vertx vertx, VertxTestContext ctx) throws VertxException {
        port = freePort();
        opts = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
        vertx.deployVerticle(new GatewayVerticle(), opts).onComplete(ctx.succeedingThenComplete());
        webClient = WebClient.create(vertx);
    }

    @Test
    void postOrder_acceptedTrue_returnsPlaced_andSendsToOrchestrator(Vertx vertx, VertxTestContext ctx) {
        Checkpoint orchestratorConsumed = ctx.checkpoint();
        Checkpoint orderAccepted = ctx.checkpoint();

        MessageConsumer<JsonObject> orchestratorConsumer = vertx.eventBus().consumer(ORDER_ORCHESTRATOR);
        orchestratorConsumer.handler(msg -> {
            JsonObject body = msg.body();
            ctx.verify(() -> {
                body.getString("status").equals("PLACED");
            });
            msg.reply(new JsonObject().put("accepted", true));
            orchestratorConsumer.unregister();
            orchestratorConsumed.flag();
        });

        JsonObject req = sampleOrderRequest();

        webClient.post(port, HOST, ORDER_PATH)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(req)
                .onSuccess(resp -> {
                    ctx.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(HttpStatus.SC_OK);
                        JsonObject body = resp.bodyAsJsonObject();
                        assertThat(body.getString("status")).isEqualTo("PLACED");
                        assertThat(body.getString("orderId")).isNotNull();
                        orderAccepted.flag();
                    });

                });
    }

    @Test
    void postOrder_acceptedFalse_returnsFailed(Vertx vertx, VertxTestContext ctx) {
        Checkpoint msgReceivedByOrchestrator = ctx.checkpoint();
        Checkpoint orderAccepted = ctx.checkpoint();
        MessageConsumer<JsonObject> orchestratorConsumer = vertx.eventBus().consumer(ORDER_ORCHESTRATOR);

        orchestratorConsumer.handler(msg -> {
            JsonObject body = msg.body();
            ctx.verify(() -> {
                assertThat(body.getString("orderId")).isNotEmpty();
                assertThat(body.getString("status")).isEqualTo("PLACED");
            });
            msg.reply(new JsonObject().put("accepted", false));
            orchestratorConsumer.unregister();
            msgReceivedByOrchestrator.flag();
        });

        JsonObject req = sampleOrderRequest();
        webClient.post(port, HOST, ORDER_PATH)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(req)
                .onSuccess(resp -> {
                    ctx.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(HttpStatus.SC_OK);
                        JsonObject body = resp.bodyAsJsonObject();
                        assertThat(body.getString("status")).isEqualTo("FAILED");
                        orderAccepted.flag();
                    });
                });
    }

    @Test
    void postOrder_orchestratorFails_returns500(Vertx vertx, VertxTestContext ctx) {
        Checkpoint msgReceivedByOrchestrator = ctx.checkpoint();
        Checkpoint orderAccepted = ctx.checkpoint();

        MessageConsumer<JsonObject> orchestratorConsumer = vertx.eventBus().consumer(ORDER_ORCHESTRATOR);
        orchestratorConsumer.handler(msg -> {
            JsonObject body = msg.body();
            ctx.verify(() -> {
                assertThat(body.getString("orderId")).isNotNull();
            });
            msg.reply(new JsonObject());
            orchestratorConsumer.unregister();
            msgReceivedByOrchestrator.flag();
        });

        JsonObject req = sampleOrderRequest();
        webClient.post(port, HOST, ORDER_PATH)
                .putHeader("Content-Type", "application/json")
                .sendJsonObject(req)
                .onSuccess(resp -> {
                    ctx.verify(() -> {
                        assertThat(resp.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                        orderAccepted.flag();
                    });
                });
    }

    private static JsonObject sampleOrderRequest() {
        return new JsonObject()
                .put("customerId", "c-1")
                .put("items", new JsonArray()
                        .add(new JsonObject()
                                .put("productId", "p-1")
                                .put("name", "Pepperoni")
                                .put("size", "LARGE")
                                .put("quantity", 1)
                                .put("price", 18.99)))
                .put("totalAmount", 18.99);
    }

    private static int freePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
