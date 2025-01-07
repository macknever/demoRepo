package com.lawrence.kafka.cassandra;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.google.inject.Inject;
import com.lawrence.kafka.guice.annotations.AuthorTable;
import com.lawrence.kafka.guice.annotations.MainKeySpace;

import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.ResultSet;

/**
 * Interacts with author table in cassandra.
 */
public class AuthorRepository implements CassandraRepository<Author> {
    private static final Logger LOG = LoggerFactory.getLogger(AuthorRepository.class);
    private static final String AUTHOR_ID_COLUMN = "author_id";
    private static final String AUTHOR_NAME_COLUMN = "author_name";
    private static final String PERSONAL_NAME_COLUMN = "personal_name";

    private final String keyspace;
    private final String tableName;

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + "?.?" + " ("
            + AUTHOR_ID_COLUMN + " TEXT PRIMARY KEY,"
            + AUTHOR_NAME_COLUMN + " TEXT,"
            + PERSONAL_NAME_COLUMN + " TEXT"
            + ")";

    private static final String INSERT_QUERY = String.format("INSERT INTO ?.? (%s,%s,%s) VALUES (?,?,?)",
            AUTHOR_ID_COLUMN, AUTHOR_NAME_COLUMN, PERSONAL_NAME_COLUMN);

    private final CassandraClient cassandraClient;
    private PreparedStatement createAuthorStatement;
    private PreparedStatement insertAuthorStatement;

    @Inject
    public AuthorRepository(final CassandraClient cassandraClient, @MainKeySpace final String keyspace,
            @AuthorTable final String tableName) {
        this.cassandraClient = cassandraClient;
        this.keyspace = keyspace;
        this.tableName = tableName;

        cassandraClient.prepare(CREATE_TABLE_QUERY)
                .onComplete(psr -> {
                    if (psr.succeeded()) {
                        LOG.info("The create table query has successfully been prepared");
                        createAuthorStatement = psr.result();
                    } else {
                        LOG.error("The create table query has failed to prepare", psr.cause());
                    }
                });

        cassandraClient.prepare(INSERT_QUERY)
                .onComplete(psr -> {
                    if (psr.succeeded()) {
                        LOG.info("The insert query has successfully been prepared");
                        insertAuthorStatement = psr.result();
                    } else {
                        LOG.error("The insert query has failed to prepare", psr.cause());
                    }
                });

    }

    @Override
    public void createTable() {
        cassandraClient.execute(createAuthorStatement.bind(keyspace, tableName))
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
        cassandraClient.execute(insertAuthorStatement.bind(author.id(), author.name(), author.personalName()))
                .onComplete(done -> {
                    ResultSet rs = done.result();
                    if (rs.wasApplied()) {
                        LOG.info("Author has been inserted successfully");
                    } else {
                        LOG.error("Author failed to be inserted");
                    }
                });
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
