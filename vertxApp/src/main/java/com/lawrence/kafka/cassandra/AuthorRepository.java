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
import io.vertx.core.Future;
import io.vertx.core.Promise;

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

    private static final String CREATE_TABLE_QUERY_FORMAT = "CREATE TABLE IF NOT EXISTS "
            + "%s.%s" + " ("
            + AUTHOR_ID_COLUMN + " TEXT PRIMARY KEY,"
            + AUTHOR_NAME_COLUMN + " TEXT,"
            + PERSONAL_NAME_COLUMN + " TEXT"
            + ")";

    private static final String INSERT_QUERY_FORMAT = "INSERT INTO %s.%s (%s,%s,%s) VALUES (?,?,?)";
    private static final String SELECT_QUERY_FORMAT = "SELECT * FROM %s.%s WHERE %s=?";

    private final CassandraClient cassandraClient;
    private PreparedStatement createAuthorStatement;
    private PreparedStatement insertAuthorStatement;
    private PreparedStatement findByIdStatement;
    private PreparedStatement findByNameStatement;

    @Inject
    public AuthorRepository(final CassandraClient cassandraClient, @MainKeySpace final String keyspace,
            @AuthorTable final String tableName) {
        this.cassandraClient = cassandraClient;
        this.keyspace = keyspace;
        this.tableName = tableName;

        initRepository();
    }

    private void initRepository() {
        final String createTableQuery = String.format(CREATE_TABLE_QUERY_FORMAT, keyspace, tableName);

        cassandraClient.prepare(createTableQuery)
                .onComplete(ps -> {
                    if (ps.succeeded()) {
                        LOG.info("The create table query has successfully been prepared");
                        createAuthorStatement = ps.result();
                        executeCreateTable();
                    } else {
                        LOG.error("The create table query has failed to prepare", ps.cause());
                    }
                });
    }

    private void executeCreateTable() {
        if (createAuthorStatement == null) {
            LOG.error("Create table statement is not prepared yet");
        }

        cassandraClient.execute(createAuthorStatement.getQuery())
                .onComplete(done -> {
                    ResultSet rs = done.result();
                    if (rs.wasApplied()) {
                        LOG.info("Table has been created successfully. Will create prepared statements");
                        prepareQueries();
                    } else {
                        LOG.error("Table has not been created");
                    }
                });
    }

    private void prepareQueries() {
        final String insertQuery = String.format(INSERT_QUERY_FORMAT, keyspace, tableName,
                AUTHOR_ID_COLUMN, AUTHOR_NAME_COLUMN, PERSONAL_NAME_COLUMN);
        final String findByIdQuery = String.format(SELECT_QUERY_FORMAT, keyspace, tableName, AUTHOR_ID_COLUMN);
        final String findByNameQuery = String.format(SELECT_QUERY_FORMAT, keyspace, tableName,
                AUTHOR_NAME_COLUMN);

        cassandraClient.prepare(insertQuery)
                .onComplete(psr -> {
                    if (psr.succeeded()) {
                        LOG.info("The insert query has successfully been prepared");
                        insertAuthorStatement = psr.result();
                    } else {
                        LOG.error("The insert query has failed to prepare", psr.cause());
                    }
                });


        cassandraClient.prepare(findByIdQuery)
                .onComplete(psr -> {
                    if (psr.succeeded()) {
                        LOG.info("The find by id query has successfully been prepared");
                        findByIdStatement = psr.result();
                    } else {
                        LOG.error("The find by id query has failed to prepare", psr.cause());
                    }
                });

        cassandraClient.prepare(findByNameQuery)
                .onComplete(psr -> {
                    if (psr.succeeded()) {
                        LOG.info("The find by name query has successfully been prepared");
                        findByNameStatement = psr.result();
                    } else {
                        LOG.error("The find by name query has failed to prepare", psr.cause());
                    }
                });
    }

    @Override
    public Future<Void> insert(Author author) {
        Promise<Void> promise = Promise.promise();
        cassandraClient.execute(insertAuthorStatement.bind(author.id(), author.name(), author.personalName()))
                .onComplete(done -> {
                    ResultSet rs = done.result();
                    if (rs.wasApplied()) {
                        LOG.info("Author has been inserted successfully");
                    } else {
                        LOG.error("Author failed to be inserted");
                    }
                });
        return promise.future();
    }

    @Override
    public Future<Author> findById(String id) {
        Promise<Author> promise = Promise.promise();
        cassandraClient.execute(findByIdStatement.bind(id))
                .onComplete(done -> {
                    ResultSet rs = done.result();
                    promise.complete(rs.one().get(0, Author.class));
                });

        return promise.future();
    }

    @Override
    public Future<List<Author>> findByName(String name) {
        Promise<List<Author>> promise = Promise.promise();
        cassandraClient.execute(findByNameStatement.bind(name))
                .onComplete(done -> {
                    if (done.succeeded()) {
                        ResultSet rs = done.result();
                        List<Author> authors = rs.all().result().stream()
                                .map(row -> new Author(
                                        row.getString(AUTHOR_ID_COLUMN),
                                        row.getString(AUTHOR_NAME_COLUMN),
                                        row.getString(PERSONAL_NAME_COLUMN))
                                ).toList();
                    }
                });
        return promise.future();
    }
}
