package com.lawrence.kafka.guice.module;

import static com.lawrence.kafka.guice.Constants.BOOTSTRAP_SERVERS;
import static com.lawrence.kafka.guice.Constants.KEY_SERIALIZER;
import static com.lawrence.kafka.guice.Constants.SESSION_TIMEOUT_MS;
import static com.lawrence.kafka.guice.Constants.VALUE_DESERIALIZER;
import static com.lawrence.kafka.guice.Constants.VALUE_SERIALIZER;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.lawrence.kafka.guice.injector.PropertiesInjector;
import com.lawrence.kafka.producer.KafkaWebAppVerticle;

import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.impl.KafkaProducerImpl;

/**
 * Create instance of kafka producer and consumer.
 */
public class KafkaModule extends AbstractModule {

    public static final String KAFKA_PRODUCER_CONFIG = "kafkaProducerConfig";
    public static final String KAFKA_PRODUCER = "kafkaProducer";
    public static final String KAFKA_CONSUMER = "kafkaConsumer";
    public static final String KAFKA_PRODUCER_VERTICLE = "kafkaProducerVerticle";

    @Provides
    @Singleton
    Vertx provideVertx() {
        return Vertx.vertx();
    }

    @Provides
    @Singleton
    @Named(KAFKA_PRODUCER_CONFIG)
    Map<String, String> producerConfig(@Named(BOOTSTRAP_SERVERS) String bootstrapServers,
            @Named(KEY_SERIALIZER) String keySerializer, @Named(VALUE_SERIALIZER) String valueSerializer,
            @Named(SESSION_TIMEOUT_MS) String sessionTimeoutMs) {
        Map<String, String> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS, bootstrapServers);
        config.put(KEY_SERIALIZER, keySerializer);
        config.put(VALUE_SERIALIZER, valueSerializer);
        config.put(SESSION_TIMEOUT_MS, sessionTimeoutMs);
        return config;
    }

    @Provides
    @Singleton
    @Named(KAFKA_PRODUCER)
    public KafkaProducer<String, String> getKafkaProducer(Vertx vertx,
            @Named(KAFKA_PRODUCER_CONFIG) Map<String, String> config) {
        return KafkaProducer.createShared(vertx, "the-producer", config);
    }

    @Provides
    @Singleton
    public KafkaWebAppVerticle getKafkaWebAppVerticle(
            @Named(KAFKA_PRODUCER) KafkaProducer<String, String> kafkaProducer) {
        return new KafkaWebAppVerticle(kafkaProducer);
    }

}
