package com.lawrence.kafka.cassandra;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.google.inject.Inject;

import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.ResultSet;

public class AuthorRepository implements CassandraRepository<Author> {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorRepository.class);

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + "KEYSPACE_NAME = ?" + "." + "TABLE_NAME = ?" + " ("
            + "author_id TEXT PRIMARY KEY,"
            + "author_name TEXT,"
            + "personal_name TEXT"
            + ")";

    private final CassandraClient cassandraClient;
    private PreparedStatement createAuthorStatement;

    @Inject
    public AuthorRepository(final CassandraClient cassandraClient) {
        this.cassandraClient = cassandraClient;
        cassandraClient.prepare(CREATE_TABLE_QUERY)
                .onComplete(psr -> {
                    if (psr.succeeded()) {
                        LOG.info("The create table query has successfully been prepared");
                        createAuthorStatement = psr.result();
                    } else {
                        LOG.error("The create table query has failed to prepare", psr.cause());
                    }
                });

    }

    @Override
    public void createTable(final String keySpace, final String tableName) {
        cassandraClient.execute(createAuthorStatement.bind(keySpace, tableName))
                .onComplete(done -> {
                    ResultSet rs = done.result();
                    if (rs.wasApplied()) {
                        LOG.info("Table has been created successfully");
                    } else {
                        LOG.error("Table has not been created");
                    }
                });
    }

    @Override
    public void insert(Author author) {

    }

    @Override
    public <T> T findById(String id) {
        return null;
    }

    @Override
    public <T> List<T> findByName(String name) {
        return List.of();
    }
}
