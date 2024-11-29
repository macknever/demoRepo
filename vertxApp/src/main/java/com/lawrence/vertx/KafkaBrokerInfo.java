package com.lawrence.vertx;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;

public class KafkaBrokerInfo {
    public static void main(String[] args) {
        // Kafka configuration properties
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:56185"); // Update with your broker address

        // Create an AdminClient
        try (AdminClient adminClient = AdminClient.create(props)) {
            // Describe the cluster
            DescribeClusterResult clusterResult = adminClient.describeCluster();

            // Fetch and print broker information
            System.out.println("Cluster ID: " + clusterResult.clusterId().get());
            System.out.println("Controller ID: " + clusterResult.controller().get().id());
            System.out.println("Broker IDs:");
            clusterResult.nodes().get().forEach(node -> {
                System.out.println(" - Broker ID: " + node.id() + ", Host: " + node.host());
            });
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
