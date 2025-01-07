package com.lawrence.kafka.guice.injector;

import com.google.inject.Injector;
import com.lawrence.kafka.guice.module.CassandraSessionModule;
import com.lawrence.kafka.guice.module.KafkaModule;

public class MainInjector {
    private MainInjector() {
        throw new AssertionError("Should not instantiated");
    }

    private static class LAZY {
        private static Injector INSTANCE = PropertiesInjector.getInstance()
                .createChildInjector(new KafkaModule());
    }

    public static Injector getInstance() {
        return LAZY.INSTANCE;
    }
}
