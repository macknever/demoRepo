package com.lawrence.kafka.cassandra.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.google.inject.Inject;
import com.lawrence.kafka.entity.Author;
import com.lawrence.kafka.guice.annotations.AuthorTable;
import com.lawrence.kafka.guice.annotations.MainKeySpace;

import io.vertx.cassandra.CassandraClient;
import io.vertx.core.Future;

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
    private static final String SELECT_ALLOW_FILTERING_QUERY_FORMAT = "SELECT * FROM %s.%s WHERE %s=? ALLOW FILTERING";

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

    }

    @Override
    public Future<Void> initRepository() {

        final String createTableQuery = String.format(CREATE_TABLE_QUERY_FORMAT, keyspace, tableName);

        return cassandraClient.prepare(createTableQuery)
                .compose(ps -> cassandraClient.execute(ps.getQuery()))
                .onSuccess(rs -> LOG.info("Table created"))
                .compose(v -> prepareQueries())
                .onSuccess(v -> LOG.info("All Statements have been prepared"))
                .onFailure(err -> LOG.error("Fail to init repository", err));
    }

    private Future<Void> prepareQueries() {
        final String insertQuery = String.format(INSERT_QUERY_FORMAT, keyspace, tableName,
                AUTHOR_ID_COLUMN, AUTHOR_NAME_COLUMN, PERSONAL_NAME_COLUMN);
        final String findByIdQuery = String.format(SELECT_QUERY_FORMAT, keyspace, tableName, AUTHOR_ID_COLUMN);
        final String findByNameQuery = String.format(SELECT_ALLOW_FILTERING_QUERY_FORMAT, keyspace, tableName,
                AUTHOR_NAME_COLUMN);

        Future<PreparedStatement> insertFuture = cassandraClient.prepare(insertQuery);
        Future<PreparedStatement> findByIdFuture = cassandraClient.prepare(findByIdQuery);
        Future<PreparedStatement> findByNameFuture = cassandraClient.prepare(findByNameQuery);

        return Future.all(insertFuture, findByIdFuture, findByNameFuture)
                .map(composite -> {
                    @SuppressWarnings("unchecked")
                    List<PreparedStatement> statements = composite.result().list();

                    this.insertAuthorStatement = statements.get(0);
                    this.findByIdStatement = statements.get(1);
                    this.findByNameStatement = statements.get(2);

                    return null;
                });

    }

    @Override
    public Future<Void> insert(Author author) {
        // Ensure statement is not null if the user forgot to call initRepository()
        if (insertAuthorStatement == null) {
            return Future.failedFuture("Repository not initialized; call initRepository() first!");
        }

        return cassandraClient.execute(insertAuthorStatement.bind(
                        author.id(), author.name(), author.personalName()
                ))
                .compose(rs -> {
                    if (rs.wasApplied()) {
                        LOG.info("Author inserted successfully: {}", author.id());
                        return Future.succeededFuture();
                    } else {
                        LOG.error("Author insertion failed for {}", author.id());
                        return Future.failedFuture("Insert was not applied.");
                    }
                });
    }

    @Override
    public Future<Author> findById(String id) {
        if (findByIdStatement == null) {
            return Future.failedFuture("Repository not initialized; call initRepository() first!");
        }

        return cassandraClient.execute(findByIdStatement.bind(id))
                .compose(rs -> {
                    Row row = rs.one();
                    if (row != null) {
                        // Construct Author from row
                        Author author = new Author(
                                row.getString(AUTHOR_ID_COLUMN),
                                row.getString(AUTHOR_NAME_COLUMN),
                                row.getString(PERSONAL_NAME_COLUMN)
                        );
                        return Future.succeededFuture(author);
                    } else {
                        // Return null or a "not found" error
                        LOG.info("Author not found for ID {}", id);
                        return Future.succeededFuture(null);
                    }
                });
    }

    @Override
    public Future<List<Author>> findByName(String name) {
        if (findByNameStatement == null) {
            return Future.failedFuture("Repository not initialized; call initRepository() first!");
        }

        return cassandraClient.execute(findByNameStatement.bind(name))
                .compose(rs -> {
                    // Depending on your Vert.x Cassandra version,
                    // rs.all() might be synchronous or return a Future<RowSet<Row>>.
                    // If it's synchronous, that's fine. Otherwise adapt accordingly.
                    List<Row> rows = rs.all().result();

                    List<Author> authors = rows.stream()
                            .map(row -> new Author(
                                    row.getString(AUTHOR_ID_COLUMN),
                                    row.getString(AUTHOR_NAME_COLUMN),
                                    row.getString(PERSONAL_NAME_COLUMN)
                            ))
                            .toList();

                    LOG.info("{} author(s) found by name '{}'", authors.size(), name);
                    return Future.succeededFuture(authors);
                });
    }
}
