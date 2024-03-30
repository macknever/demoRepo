package com.lawrence.dataloaderspring.config;

import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@EnableConfigurationProperties(DatastaxProperties.class)
public class CassandraSessionConfig {

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DatastaxProperties properties) {
        Path bundlePath = properties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundlePath);
    }

}
