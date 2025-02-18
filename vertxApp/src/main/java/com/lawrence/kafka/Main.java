package com.lawrence.kafka;

import static com.lawrence.kafka.guice.module.KafkaModule.KAFKA_CONSUMER;
import static com.lawrence.kafka.guice.module.KafkaModule.KAFKA_PRODUCER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.lawrence.kafka.cassandra.service.AuthorService;
import com.lawrence.kafka.consumer.KafkaConsumerVerticle;
import com.lawrence.kafka.entity.Author;
import com.lawrence.kafka.cassandra.repository.AuthorRepository;
import com.lawrence.kafka.guice.injector.MainInjector;
import com.lawrence.kafka.producer.KafkaWebAppVerticle;

import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String ALLOWED_TOPIC = "cp.msg-mds.local.ca.nvan.directory.changelog";

    public static void main(String[] args) {
        // Create a Vertx instance
        Injector injector = MainInjector.getInstance();


        Vertx vertx = injector.getInstance(Vertx.class);

        KafkaProducer<String, String> producer =
                injector.getInstance(
                        Key.get(new TypeLiteral<>() {
                        }, Names.named(KAFKA_PRODUCER)));

        AuthorService authorService = injector.getInstance(AuthorService.class);
        KafkaConsumer<String, String> consumer =
                injector.getInstance(Key.get(new TypeLiteral<>() {
                }, Names.named(KAFKA_CONSUMER)));


        // Deploy the KafkaWebAppVerticle
        vertx.deployVerticle(new KafkaWebAppVerticle(producer), res -> {
            if (res.succeeded()) {
                LOG.info("KafkaWebAppVerticle deployed successfully.");
            } else {
                LOG.error("Failed to deploy KafkaWebAppVerticle: {}", res.cause().getMessage());
            }
        });

        vertx.deployVerticle(new KafkaConsumerVerticle(consumer, ALLOWED_TOPIC, authorService), res -> {
            if (res.succeeded()) {
                LOG.info("KafkaConsumerVerticle deployed successfully.");
                authorService.initRepository();
            } else {
                LOG.error("Failed to deploy KafkaConsumerVerticle: {}", res.cause().getMessage());
            }
        });

    }
}
