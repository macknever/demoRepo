package com.lawrence.kafka.guice.module;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.lawrence.kafka.guice.annotations.AuthorTable;
import com.lawrence.kafka.guice.annotations.MainKeySpace;

public class PropertiesModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesModule.class);
    private static final String PROPERTIES_PATH = "app.properties";

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_PATH)) {
            properties.load(inputStream);
            Names.bindProperties(binder(), properties);
            bind(String.class).annotatedWith(MainKeySpace.class)
                    .toInstance(properties.getProperty("datastax.astra.keyspace"));
            bind(String.class).annotatedWith(AuthorTable.class)
                    .toInstance(properties.getProperty("datastax.astra.author"));
        } catch (IOException e) {
            LOG.error("cant not load properties");
        }
    }
}
