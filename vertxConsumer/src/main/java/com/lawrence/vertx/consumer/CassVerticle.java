package com.lawrence.vertx.consumer;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class CassVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(CassVerticle.class);

    private static final String KEY_SPACE = "simple_keyspace";
    public static final String TABLE = "simple_table";

    private static final String INSERT_STATEMENT = "INSERT INTO simple_table " +
            "(id, name, content, created_at) VALUES (?,?,?,?)";

    @Override
    public void start() {
        CassandraClientOptions options = new CassandraClientOptions()
                .addContactPoint("localhost", 30942)
                .setKeyspace("simple_keyspace");

        CassandraClient client = CassandraClient.create(vertx, options);

        Future<PreparedStatement> preparedStatementFuture = client.prepare(INSERT_STATEMENT)
                .onComplete(psResult -> {
                    if (psResult.succeeded()) {
                        LOG.info("The Query has been successfully prepared");
                    } else {
                        LOG.error("Unable to prepare query");
                        psResult.cause().printStackTrace();
                    }
                });

        PreparedStatement insertStatement = preparedStatementFuture.result();
        client.execute(insertStatement.bind(UUID.randomUUID(), "simple_name", "random_content", Instant.now()));

    }
}
