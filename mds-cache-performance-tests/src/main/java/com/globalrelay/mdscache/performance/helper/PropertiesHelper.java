package com.globalrelay.mdscache.performance.helper;

public class PropertiesHelper {
    private static final String DEFAULT_TARGET_ENVIRONMENT = "local";

    private PropertiesHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getTargetEnvironment() {
        String config = System.getProperty("testing.target.env");
        if (config == null) {
            config = System.getenv("testing.target.env");
        }

        if (config == null) {
            config = DEFAULT_TARGET_ENVIRONMENT;
        }

        return config;
    }
}
