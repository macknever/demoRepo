package com.lawrence.vertx.vertxDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;

public class Deployer extends AbstractVerticle {
    public static final Logger LOG = LoggerFactory.getLogger(Deployer.class);

    @Override
    public void start() {
        long delay = 1000;
        for (int i = 0; i < 50; i++) {
            vertx.setTimer(delay, id -> deploy());
            delay += 1000;
        }
    }

    private void deploy() {
        vertx.deployVerticle(new EmptyVerticle(), ar -> {
            if (ar.succeeded()) {
                String id = ar.result();
                LOG.info("Successfully deployed {}", id);
                vertx.setTimer(5000, tid -> undeployLater(id));
            } else {
                LOG.error("Error while deploying {}", ar.cause());
            }
        });
    }

    private void undeployLater(String id) {
        vertx.undeploy(id, ar -> {
            if (ar.succeeded()) {
                LOG.info("{} was undeployed", id);
            } else {
                LOG.error("{} could not be undeployed", id);
            }
        });
    }
}
