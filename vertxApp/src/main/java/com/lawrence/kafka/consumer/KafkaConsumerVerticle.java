package com.lawrence.kafka.consumer;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawrence.kafka.entity.Author;
import com.lawrence.kafka.cassandra.service.AuthorService;
import com.lawrence.kafka.util.AuthorUtil;

import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.consumer.KafkaConsumerRecord;

/**
 * This verticle consumes from kafka and populates the data into cassandra.
 */
public class KafkaConsumerVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerVerticle.class);
    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final AuthorService authorService;

    public KafkaConsumerVerticle(final KafkaConsumer<String, String> consumer, final String topic,
            final AuthorService authorService) {
        this.consumer = consumer;
        this.topic = topic;
        this.authorService = authorService;
    }

    @Override
    public void start() {
        consumer
                .subscribe(topic)
                .onSuccess(v -> {
                    LOG.info("Consumer subscribed");

                    // Let's poll every second
                    vertx.setPeriodic(10, timerId ->
                            consumer
                                    .poll(Duration.ofMillis(100))
                                    .onSuccess(records -> {
                                        for (int i = 0; i < records.size(); i++) {
                                            KafkaConsumerRecord<String, String> record = records.recordAt(i);
                                            LOG.info("Consumer record: {}", record);
                                            Author author = AuthorUtil.generateAuthor(record);
                                            authorService.addAuthor(author);
                                        }
                                    })
                                    .onFailure(cause -> {
                                        LOG.error("Something went wrong when polling {}", cause.toString());
                                        vertx.cancelTimer(timerId);
                                    })
                    );
                });

    }

}
