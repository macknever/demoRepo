package com.lawrence.vertx;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaTopicCreator {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicCreator.class);

    public static void main(String[] args) {
        String bootstrapServers = "192.168.59.100:32123";

        String topicName = "notifications";
        int numPartitions = 3;
        short replicationFactor = 1;

        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(properties)) {

            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);

            adminClient.createTopics(Collections.singleton(newTopic)).all().get();

            LOG.info("Topic created successfully: {}", topicName);

        } catch (ExecutionException e) {
            LOG.error("Error creating topic: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Topic creation was interrupted");
        }
    }
}

