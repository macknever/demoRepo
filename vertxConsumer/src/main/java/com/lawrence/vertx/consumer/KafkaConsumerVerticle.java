package com.lawrence.vertx.consumer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;

public class KafkaConsumerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerVerticle.class);
    Map<String, String> config = new HashMap<>();

    @Override
    public void start() {
        config.put("bootstrap.servers", "192.168.59.100:32123");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "my_group");
        config.put("auto.offset.reset", "latest");
        config.put("enable.auto.commit", "true");

        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);

        consumer
                .subscribe("notifications")
                .onSuccess(v -> {
                    System.out.println("Consumer subscribed");

                    // Let's poll every second
                    vertx.setPeriodic(1000, timerId ->
                            consumer
                                    .poll(Duration.ofMillis(100))
                                    .onSuccess(records -> {
                                        for (int i = 0; i < records.size(); i++) {
                                            KafkaConsumerRecord<String, String> record = records.recordAt(i);
                                            System.out.println("key=" + record.key() + ",value=" + record.value() +
                                                    ",partition=" + record.partition() + ",offset=" + record.offset());
                                        }
                                    })
                                    .onFailure(cause -> {
                                        System.out.println("Something went wrong when polling " + cause.toString());
                                        cause.printStackTrace();

                                        vertx.cancelTimer(timerId);
                                    })
                    );
                });

    }

}
