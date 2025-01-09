package com.lawrence.kafka.cassandra;

import java.util.List;

import io.vertx.core.Future;
import io.vertx.core.Promise;

public interface CassandraRepository<T> {

    Future<Void> insert(T t);

    Future<T> findById(String id);

    Future<List<T>> findByName(String name);
}
