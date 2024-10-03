package com.lawrence.vertx.vertxDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle {
    private final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.setPeriodic(5000, id -> LOG.info("tick"));

        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        }).listen(8888).onComplete(http -> {
            if (http.succeeded()) {
                startPromise.complete();
                LOG.info("HTTP server started on port 8888");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    public static void main(String[] args) {


        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Deployer());
    }
}
