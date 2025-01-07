package com.lawrence.kafka.guice.module;

import java.nio.file.Paths;
import java.util.Objects;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.Vertx;

public class CassandraModule extends AbstractModule {

    private static final String DATASTAX_ASTRA_TOKEN_PROP = "datastax.astra.token";
    private static final String DATASTAX_ASTRA_ID_PROP = "datastax.astra.id";
    private static final String DATASTAX_ASTRA_KEYSPACE_PROP = "datastax.astra.keyspace";
    private static final String DATASTAX_ASTRA_BUNDLE_PROP = "datastax.astra.bundle.path";

    @Provides
    public CqlSessionBuilder provideCqlSessionBuilder(@Named(DATASTAX_ASTRA_BUNDLE_PROP) String bundlePath,
            @Named(DATASTAX_ASTRA_TOKEN_PROP) String token,
            @Named(DATASTAX_ASTRA_KEYSPACE_PROP) String keyspace
    ) {
        String fullPath = Objects.requireNonNull(getClass().getClassLoader().getResource(bundlePath)).getPath();
        return CqlSession.builder().withCloudSecureConnectBundle(Paths.get(fullPath))
                .withAuthCredentials("token", token)
                .withKeyspace(keyspace);
    }

    @Provides
    @Singleton
    public CassandraClientOptions providesCassandraClientOptions(CqlSessionBuilder builder) {
        return new CassandraClientOptions(builder);
    }

    @Provides
    public CassandraClient providesCassandraClient(final Vertx vertx, CassandraClientOptions options) {
        return CassandraClient.create(vertx, options);
    }

}
