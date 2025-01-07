package com.lawrence.kafka.cassandra;

import java.util.List;

public interface CassandraRepository<T> {
    void createTable();

    void insert(T t);

    <T> T findById(String id);

    <T> List<T> findByName(String name);
}
