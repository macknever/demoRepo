package com.lawrence.vertx;

import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

public class KafkaHealthCheck {
    public static void main(String[] args) {
        String brokerUrl = "192.168.49.2:30092";

        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);

        try (AdminClient adminClient = AdminClient.create(properties)) {
            // Try fetching cluster metadata
            adminClient.describeCluster().clusterId().get();
            System.out.println("Kafka is running at " + brokerUrl);
        } catch (Exception e) {
            System.err.println("Failed to connect to Kafka at " + brokerUrl);
            e.printStackTrace();
        }
    }
}
