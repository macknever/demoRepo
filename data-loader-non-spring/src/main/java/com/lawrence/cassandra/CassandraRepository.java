package com.lawrence.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;

import java.util.List;

public interface CassandraRepository<T> {
    void createTable();
    void insert(T t);

    <T> T findById(String id);
    <T> List<T> findByName(String name);
}
