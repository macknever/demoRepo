package com.lawrence.cassandra;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Author {
    public static final String AUTHOR_ID = "author_id";
    public static final String AUTHOR_NAME = "author_name";
    public static final String PERSONAL_NAME = "personal_name";

    private String author_id;
    private String author_name;
    private String personal_name;

    public Author(String author_id, String author_name, String personal_name) {
        this.author_id = author_id;
        this.author_name = author_name;
        this.personal_name = personal_name;
    }

    public Author() {}

    public static Author fromRow(Row row) {
        return new Author(row.getString(AUTHOR_ID), row.getString(AUTHOR_NAME), row.getString(PERSONAL_NAME));
    }

    public static String generateInsertQuery(final String keyspace, final String tableName) {
        return "INSERT INTO " + keyspace + "." + tableName +
                " (" + AUTHOR_ID + "," +  AUTHOR_NAME + "," +  PERSONAL_NAME + ") VALUES (?, ?, ?)";
    }
}
