package com.lawrence.dataloaderspring.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "datastax.astra")
@Getter
@Setter
public class DatastaxProperties {
    // This name has to be converted from properties key
    private File secureConnectBundle;
}
