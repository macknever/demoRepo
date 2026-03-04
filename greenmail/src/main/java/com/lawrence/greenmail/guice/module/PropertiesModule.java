package com.lawrence.greenmail.guice.module;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import javax.annotation.Nonnull;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class PropertiesModule extends AbstractModule {
    @Override
    protected void configure() {
        Properties properties = new Properties();
        try {
            properties = getPropertiesFromResource("application.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Names.bindProperties(binder(), properties);

    }

    private static Properties getPropertiesFromResource(@Nonnull String resource) throws IOException {
        Objects.requireNonNull(resource);
        Properties properties = new Properties();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            properties.load(is);
        }

        return properties;
    }
}
