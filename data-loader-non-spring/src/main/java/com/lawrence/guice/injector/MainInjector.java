package com.lawrence.guice.injector;


import com.google.inject.Injector;
import com.lawrence.guice.module.CassandraSessionModule;

public class MainInjector {
    private MainInjector() {
        throw new AssertionError("Should not instantiated");
    }

    private static class LAZY {
        private static Injector INSTANCE = PropertiesInjector.getInstance().createChildInjector(new CassandraSessionModule());
    }

    public static Injector getInstance() {
        return LAZY.INSTANCE;
    }
}
