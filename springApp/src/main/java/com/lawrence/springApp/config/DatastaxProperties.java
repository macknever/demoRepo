package com.lawrence.springApp.config;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "datastax.astra")
@Getter
@Setter
public class DatastaxProperties {
    // This name has to be converted from properties key
    private File secureConnectBundle;
}
