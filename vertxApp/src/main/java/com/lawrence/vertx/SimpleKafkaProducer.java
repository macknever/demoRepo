package com.lawrence.vertx;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SimpleKafkaProducer {
    private static final String TOPIC = "notifications";
    private static final String BROKER = "127.0.0.1:50448";

    public static void main(String[] args) {
        // Configure Kafka producer properties
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", BROKER);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // Create KafkaProducer
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        try {
            // Send 10 messages to the topic
            for (int i = 1; i <= 10; i++) {
                String key = "key-" + i;
                String value = "value-" + i;
                producer.send(new ProducerRecord<>(TOPIC, key, value), (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("Error producing message: " + exception.getMessage());
                    } else {
                        System.out.printf("Produced message to topic %s partition %d with offset %d%n",
                                metadata.topic(), metadata.partition(), metadata.offset());
                    }
                });
            }
        } finally {
            // Close producer
            producer.close();
        }
    }
}

