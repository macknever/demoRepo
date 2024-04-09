package com.lawrence.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.google.inject.Inject;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.K;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lawrence.cassandra.Author.AUTHOR_NAME;


public class AuthorRepository implements CassandraRepository<Author> {

    private final CqlSession session;
    private final String TABLE_NAME;
    private final String KEYSPACE_NAME;
    private final String INSERT_QUERY;
    private final String FIND_BY_ID_QUERY;
    private final String FIND_BY_NAME;
    private final String FIND_ALL_QUERY;

    @Inject
    public AuthorRepository(final CqlSession session) {
        this.session = session;
        this.TABLE_NAME = "author_by_id_non_spring";
        this.KEYSPACE_NAME = "main";
        this.INSERT_QUERY = Author.generateInsertQuery(KEYSPACE_NAME, TABLE_NAME);
        this.FIND_BY_ID_QUERY = "SELECT * FROM " + KEYSPACE_NAME + "." + TABLE_NAME +
                " WHERE author_id= ?";
        this.FIND_BY_NAME = "SELECT * FROM " + KEYSPACE_NAME + "." + TABLE_NAME +
                " WHERE author_name= ? ALLOW FILTERING";
        this.FIND_ALL_QUERY = "SELECT * FROM " + KEYSPACE_NAME + "." + TABLE_NAME;
    }

    @Override
    public void createTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "
                + KEYSPACE_NAME + "." + TABLE_NAME + " ("
                + "author_id TEXT PRIMARY KEY,"
                + "author_name TEXT,"
                + "personal_name TEXT"
                + ")";
        session.execute(createTableQuery);
        TableMetadata tableMetadata = session.getMetadata().getKeyspace(CqlIdentifier.fromCql(KEYSPACE_NAME))
                .flatMap(ks -> ks.getTable(CqlIdentifier.fromCql(TABLE_NAME)))
                .orElseThrow(() -> new RuntimeException("Table not found"));

        System.out.println("Table created: " + tableMetadata.describe(true));
    }

    @Override
    public void insert(Author author) {
        PreparedStatement insertStatement = session.prepare(INSERT_QUERY);
        BoundStatement boundStatement =
                insertStatement.bind(author.getAuthor_id(), author.getAuthor_name(), author.getPersonal_name());
        session.execute(boundStatement);
        System.out.println("insert successful");
    }

    @Override
    public Author findById(String id) {
        BoundStatement boundStatement = session.prepare(FIND_BY_ID_QUERY).bind(id);
        ResultSet resultSet = session.execute(boundStatement);
        return Author.fromRow(Objects.requireNonNull(resultSet.one()));
    }


    public List<Author> findByNameParallel(String name) {
        ResultSet resultSet = session.execute(FIND_ALL_QUERY);
        List<Row> rows = resultSet.all().stream().parallel()
                .filter(row -> Objects.equals(row.getString(AUTHOR_NAME), name)).toList();
        return rows.stream().map(Author::fromRow).toList();
    }

    @Override
    public List<Author> findByName(String name) {
        ResultSet resultSet = session.execute(FIND_ALL_QUERY);
        List<Row> rows = resultSet.all().stream()
                .filter(row -> Objects.equals(row.getString(AUTHOR_NAME), name)).toList();
        return rows.stream().map(Author::fromRow).toList();
    }

    public List<Author> findByNameWithFilter(String name) {
        BoundStatement boundStatement = session.prepare(FIND_BY_NAME).bind(name);
        ResultSet resultSet = session.execute(boundStatement);
        return resultSet.all().stream().map(Author::fromRow).toList();
    }


}
