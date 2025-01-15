package com.lawrence.springApp.config;

import java.nio.file.Path;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DatastaxProperties.class)
public class CassandraSessionConfig {

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DatastaxProperties properties) {
        Path bundlePath = properties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundlePath);
    }

}
