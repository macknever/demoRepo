package com.lawrence.pizza.verticles;

import static com.lawrence.pizza.infrastructure.Addresses.ORDER_STATUS_UPDATES;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawrence.pizza.models.events.StatusUpdate;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

public class TrackerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(TrackerVerticle.class);

    // Store active WebSocket connections by OrderId
    // In a real Google-scale system, you'd use a distributed registry,
    // but for one server, a Map is perfect.
    private final Map<String, ServerWebSocket> clientConnections = new HashMap<>();

    @Override
    public void start() {

        // 1. Setup WebSocket Server on port 8080
        vertx.createHttpServer()
                .webSocketHandler(this::handleNewClient)
                .listen(8080)
                .onSuccess(s -> LOG.info("[Tracker] WebSocket server LIVE on 8080"));

        // 2. Listen to the Event Bus for ANY status update
        // Whenever the Orchestrator publishes an event, this runs.
        vertx.eventBus().<JsonObject>consumer(ORDER_STATUS_UPDATES, msg -> {
            StatusUpdate update = msg.body().mapTo(StatusUpdate.class);
            this.pushToClient(update);
        });
    }

    private void handleNewClient(ServerWebSocket ws) {
        // Assume the client connects via ws://localhost:8080/tracker/order-id-123
        String path = ws.path();
        String orderId = path.substring(path.lastIndexOf("/") + 1);

        if (orderId.isEmpty()) {
            ws.close();
            return;
        }

        LOG.info("[Tracker] Client connected for Order: {}", orderId);
        clientConnections.put(orderId, ws);

        // Remove the connection when the client closes the tab
        ws.closeHandler(v -> clientConnections.remove(orderId));
    }

    private void pushToClient(StatusUpdate update) {
        ServerWebSocket ws = clientConnections.get(update.orderId());

        if (ws != null && !ws.isClosed()) {
            // PUSH the data to the user immediately
            ws.writeTextMessage(JsonObject.mapFrom(update).encode());
            LOG.info("[Tracker] Pushed update to client for Order: {} ", update.orderId());
        }
    }

}
