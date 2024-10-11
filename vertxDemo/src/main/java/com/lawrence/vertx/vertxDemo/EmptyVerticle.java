package com.lawrence.vertx.vertxDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;

public class EmptyVerticle extends AbstractVerticle {
    public static final Logger LOG = LoggerFactory.getLogger(EmptyVerticle.class);

    @Override
    public void start() throws InterruptedException {
        LOG.info("Verticle started: {}", this);
    }

    @Override
    public void stop() {
        LOG.info("Verticle stopped: {}", this);
    }
}
