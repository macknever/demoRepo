package com.lawrence.springApp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("author_by_id")
public class Author {

    @Id
    @PrimaryKeyColumn(name = "author_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;

    @Column("author_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String name;

    @Column("personal_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String personalName;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Author to JSON", e);
        }
    }
}
