package com.lawrence.kafka;

import static com.lawrence.kafka.guice.Constants.VALUE_SERIALIZER;
import static com.lawrence.kafka.guice.module.KafkaModule.KAFKA_PRODUCER;
import static com.lawrence.kafka.guice.module.KafkaModule.KAFKA_PRODUCER_CONFIG;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.lawrence.kafka.guice.injector.MainInjector;
import com.lawrence.kafka.guice.injector.PropertiesInjector;
import com.lawrence.kafka.guice.module.KafkaModule;
import com.lawrence.kafka.producer.KafkaWebAppVerticle;

import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.impl.KafkaProducerImpl;

public class Main {
    public static void main(String[] args) {
        // Create a Vertx instance
        Injector injector = MainInjector.getInstance();


        Vertx vertx = injector.getInstance(Vertx.class);

        KafkaProducer<String, String> producer =
                injector.getInstance(
                        Key.get(new TypeLiteral<>() {
                        }, Names.named(KAFKA_PRODUCER)));
        
        // Deploy the KafkaWebAppVerticle
        vertx.deployVerticle(new KafkaWebAppVerticle(producer), res -> {
            if (res.succeeded()) {
                System.out.println("KafkaWebAppVerticle deployed successfully.");
            } else {
                System.err.println("Failed to deploy KafkaWebAppVerticle: " + res.cause());
            }
        });
    }
}
