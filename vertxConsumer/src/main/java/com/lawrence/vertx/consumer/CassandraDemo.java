package com.lawrence.vertx.consumer;

import java.net.InetSocketAddress;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

public class CassandraDemo {

    public static void main(String[] args) {
        // Connect to Cassandra
        try (CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", 30942))
                .withLocalDatacenter("datacenter1") // Replace with actual datacenter name
                .withConfigLoader(DriverConfigLoader.programmaticBuilder()
                        .withString(DefaultDriverOption.PROTOCOL_VERSION, "V4")
                        .build())
                .build()) {


            // Create Keyspace if it doesn't exist
            String createKeyspaceQuery = "CREATE KEYSPACE IF NOT EXISTS demo " +
                    "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}";
            session.execute(createKeyspaceQuery);
            System.out.println("Keyspace 'demo' created (if it didn't already exist).");

            // Use the keyspace
            session.execute("USE demo");

            // Create Table if it doesn't exist
            String createTableQuery = "CREATE TABLE IF NOT EXISTS employee (" +
                    "id UUID PRIMARY KEY, " +
                    "name text, " +
                    "position text)";
            session.execute(createTableQuery);
            System.out.println("Table 'employee' created (if it didn't already exist).");

            // Example Insert
            String insertQuery = "INSERT INTO employee (id, name, position) " +
                    "VALUES (uuid(), 'John Doe', 'Software Engineer')";
            session.execute(insertQuery);
            System.out.println("Inserted example row into 'employee' table.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

