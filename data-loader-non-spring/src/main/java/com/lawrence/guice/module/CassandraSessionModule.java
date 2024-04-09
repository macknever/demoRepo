package com.lawrence.guice.module;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBAdmin;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class CassandraSessionModule extends AbstractModule {

    private static final String DATASTAX_ASTRA_TOKEN_PROP="datastax.astra.token";
    private static final String DATASTAX_ASTRA_ID_PROP="datastax.astra.id";
    private static final String DATASTAX_ASTRA_KEYSPACE_PROP="datastax.astra.keyspace";
    private static final String DATASTAX_ASTRA_BUNDLE_PROP="datastax.astra.bundle.path";

    @Provides
    public CqlSessionBuilder provideCqlSessionBuilder(@Named(DATASTAX_ASTRA_BUNDLE_PROP) String bundlePath,
                                                      @Named(DATASTAX_ASTRA_TOKEN_PROP) String token,
                                                      @Named(DATASTAX_ASTRA_KEYSPACE_PROP) String keyspace
    ) {
        String fullPath = Objects.requireNonNull(getClass().getClassLoader().getResource(bundlePath)).getPath();
        return CqlSession.builder().withCloudSecureConnectBundle(Paths.get(fullPath))
                .withAuthCredentials("token",token)
                .withKeyspace(keyspace);
    }

    @Provides
    public CqlSession provideCqlSession(CqlSessionBuilder builder){
        return builder.build();
    }






}
