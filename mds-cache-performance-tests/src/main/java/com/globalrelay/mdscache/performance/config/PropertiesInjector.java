package com.globalrelay.mdscache.performance.config;

import com.globalrelay.mdscache.guice.modules.PropertiesFromResourceModule;
import com.globalrelay.mdscache.performance.helper.PropertiesHelper;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A lazy holder for a Guice injector loaded with the {@link PropertiesFromResourceModule}.
 * Child injectors can be created using this to avoid multiple loading of properties.
 * Guice injection is thread safe, and the aforementioned modules load read-only properties, so are also thread-safe.
 */
public class PropertiesInjector {
    private PropertiesInjector() {
    }

    private static class LazyHolder {
        private static final String TARGET_ENVIRONMENT = PropertiesHelper.getTargetEnvironment();
        private static final Injector INSTANCE = Guice.createInjector(
                new PropertiesFromResourceModule("config/" +
                        TARGET_ENVIRONMENT + "/performance-test.properties"));
    }

    public static Injector getInstance() {
        return LazyHolder.INSTANCE;
    }
}
