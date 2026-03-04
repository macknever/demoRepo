package com.lawrence.pizza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawrence.pizza.verticles.GatewayVerticle;
import com.lawrence.pizza.verticles.KitchenVerticle;
import com.lawrence.pizza.verticles.OrderOrchestratorVerticle;
import com.lawrence.pizza.verticles.TrackerVerticle;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // 1. Initialize the Vert.x Runtime
        Vertx vertx = Vertx.vertx();

        LOG.info("[System] Starting Domino's Reactive Backend...");

        // 2. Chain the deployments using Futures
        // We deploy the "Backend" services before the "Frontend" Gateway
        deploy(vertx, new OrderOrchestratorVerticle(), "Orchestrator")
                .compose(v -> deploy(vertx, new KitchenVerticle(), "Kitchen"))
                .compose(v -> deploy(vertx, new TrackerVerticle(), "Tracker"))
                .compose(v -> deploy(vertx, new GatewayVerticle(), "Gateway"))
                .onSuccess(id -> {
                    LOG.info("---");
                    LOG.info("✅ ALL SYSTEMS ONLINE");
                    LOG.info("REST API: http://localhost:8181/order");
                    LOG.info("WebSocket: ws://localhost:8080/tracker/{orderId}");
                    LOG.info("---");
                })
                .onFailure(err -> {
                    LOG.error("❌ SYSTEM CRASHED DURING STARTUP", err);
                    vertx.close();
                });
    }

    /**
     * Helper to deploy a verticle and log success/failure
     */
    private static Future<String> deploy(Vertx vertx, io.vertx.core.Verticle verticle, String name) {
        return vertx.deployVerticle(verticle)
                .onSuccess(id -> LOG.info("[System] " + name + " deployed successfully."));
    }
}