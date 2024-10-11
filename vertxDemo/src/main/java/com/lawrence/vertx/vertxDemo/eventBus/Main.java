package com.lawrence.vertx.vertxDemo.eventBus;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle("com.lawrence.vertx.vertxDemo.eventBus.HeatSensor",
                new DeploymentOptions().setInstances(4));
        vertx.deployVerticle("com.lawrence.vertx.vertxDemo.eventBus.Listener");
        vertx.deployVerticle("com.lawrence.vertx.vertxDemo.eventBus.SensorData");
        vertx.deployVerticle("com.lawrence.vertx.vertxDemo.eventBus.HttpServer");
    }
}
