package com.lawrence.kafka.cassandra.repository;

import java.util.List;

import io.vertx.core.Future;

public interface CassandraRepository<T> {

    Future<Void> initRepository();

    Future<Void> insert(T t);

    Future<T> findById(String id);

    Future<List<T>> findByName(String name);
}
