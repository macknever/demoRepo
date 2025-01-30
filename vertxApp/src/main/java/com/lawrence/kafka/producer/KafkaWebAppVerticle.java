package com.lawrence.kafka.producer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

public class KafkaWebAppVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaWebAppVerticle.class);
    private static int eventId = 0;
    private KafkaProducer<String, String> producer;

    public KafkaWebAppVerticle(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public void start(Promise<Void> promise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);


        router.post("/api/messages/:topic")
                .handler(ctx -> {
                    ctx.request().bodyHandler(body -> {
                        LOG.info("Received message {} from topic: {}, at {}", body, ctx.pathParam("topic"),
                                new Date().toInstant());
                        final String currentTopic = ctx.pathParam("topic");
                        final String currentContent = body.toString();
                        KafkaProducerRecord<String, String> producerRecord = KafkaProducerRecord.create(currentTopic,
                                currentContent);
                        producer.send(producerRecord).onSuccess(recordMetadata ->
                                        LOG.debug(
                                                "Message {} written on topic = {}, partition = {},offset = {} ",
                                                producerRecord.value(),
                                                recordMetadata.getTopic(),
                                                recordMetadata.getPartition(),
                                                recordMetadata.getOffset()))
                                .onFailure(err -> LOG.error("Failed to send value to Kafka, {}", err.getMessage()));
                    });
                    ctx.next();
                })
                .handler(ctx -> {
                    HttpServerResponse response = ctx.response();
                    response
                            .putHeader("content-type", "text/plain")
                            .end("Simple web vertx is working now ");
                });

        router
                .get("/api/messages")
                .handler(ctx -> {
                    final String content = ctx.body().asString();
                    ctx.response().putHeader("content-type", "text/plain").end("messages");
                });

        server
                .requestHandler(router)
                .listen(8080)
                .onSuccess(r -> promise.complete())
                .onFailure(r -> promise.fail("Failed to start the server"));
    }

}

