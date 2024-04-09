package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesModule.class);
    private static final String PROPERTIES_PATH = "app.properties";

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_PATH)) {
            properties.load(inputStream);
            Names.bindProperties(binder(), properties);
        } catch (IOException e) {
            LOG.error("cant not load properties");
        }
    }
}
