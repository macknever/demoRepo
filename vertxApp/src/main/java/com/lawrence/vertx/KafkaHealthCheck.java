package com.lawrence.vertx;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaHealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaHealthCheck.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        String brokerUrl = "192.168.59.100:32123";

        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);

        try (AdminClient adminClient = AdminClient.create(properties)) {
            // Try fetching cluster metadata
            adminClient.describeCluster().clusterId().get();
            LOG.info("Kafka is running at {}", brokerUrl);
        }
    }
}
