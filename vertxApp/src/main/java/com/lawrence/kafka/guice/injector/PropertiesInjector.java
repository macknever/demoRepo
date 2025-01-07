package com.lawrence.kafka.guice.injector;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lawrence.kafka.guice.module.PropertiesModule;

public class PropertiesInjector {
    private PropertiesInjector() {
        throw new AssertionError("Should not instantiated");
    }

    private static class LAZY {
        private static Injector INSTANCE = Guice.createInjector(new PropertiesModule());
    }

    public static Injector getInstance() {
        return LAZY.INSTANCE;
    }
}
