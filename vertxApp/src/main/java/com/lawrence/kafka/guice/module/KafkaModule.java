package com.lawrence.kafka.guice.module;

import static com.lawrence.kafka.guice.Constants.AUTO_OFFSET_RESET;
import static com.lawrence.kafka.guice.Constants.BOOTSTRAP_SERVERS;
import static com.lawrence.kafka.guice.Constants.ENABLE_AUTO_COMMIT;
import static com.lawrence.kafka.guice.Constants.GROUP_ID;
import static com.lawrence.kafka.guice.Constants.KEY_DESERIALIZER;
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
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.impl.KafkaProducerImpl;

/**
 * Create instance of kafka producer and consumer.
 */
public class KafkaModule extends AbstractModule {

    public static final String KAFKA_PRODUCER_CONFIG = "kafkaProducerConfig";
    public static final String KAFKA_CONSUMER_CONFIG = "kafkaConsumerConfig";
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
    Map<String, String> producerConfig(
            @Named(BOOTSTRAP_SERVERS) String bootstrapServers,
            @Named(KEY_SERIALIZER) String keySerializer,
            @Named(VALUE_SERIALIZER) String valueSerializer,
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
    @Named(KAFKA_CONSUMER_CONFIG)
    Map<String, String> consumerConfig(
            @Named(BOOTSTRAP_SERVERS) String bootstrapServers,
            @Named(KEY_DESERIALIZER) String keyDeserializer,
            @Named(VALUE_DESERIALIZER) String valueDeserializer,
            @Named(GROUP_ID) String groupId,
            @Named(AUTO_OFFSET_RESET) String autoOffsetReset,
            @Named(ENABLE_AUTO_COMMIT) String enableAutoCommit) {
        Map<String, String> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS, bootstrapServers);
        config.put(KEY_DESERIALIZER, keyDeserializer);
        config.put(VALUE_DESERIALIZER, valueDeserializer);
        config.put(GROUP_ID, groupId);
        config.put(AUTO_OFFSET_RESET, autoOffsetReset);
        config.put(ENABLE_AUTO_COMMIT, enableAutoCommit);

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
    @Named(KAFKA_CONSUMER)
    public KafkaConsumer<String, String> getKafkaConsumer(Vertx vertx,
            @Named(KAFKA_CONSUMER_CONFIG) Map<String, String> config) {
        return KafkaConsumer.create(vertx, config);
    }

    @Provides
    @Singleton
    public KafkaWebAppVerticle getKafkaWebAppVerticle(
            @Named(KAFKA_PRODUCER) KafkaProducer<String, String> kafkaProducer) {
        return new KafkaWebAppVerticle(kafkaProducer);
    }

}
