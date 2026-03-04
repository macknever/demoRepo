package com.lawrence.greenmail.guice.injector;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lawrence.greenmail.guice.module.PropertiesModule;

public class PropertiesInjector {
    private PropertiesInjector() {}

    private static class LAZY_HOLDER {
        private static final Injector INSTANCE = Guice.createInjector(new PropertiesModule());
    }

    public static Injector getInstance() {
        return LAZY_HOLDER.INSTANCE;
    }
}
