package com.lawrence.greenmail.guice.injector;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lawrence.greenmail.guice.module.MailModule;
import com.lawrence.greenmail.guice.module.PropertiesModule;
import com.lawrence.greenmail.guice.module.VertxModule;

public class MainInjector {
    private MainInjector() {}

    private static class LAZY_HOLDER {
        private static final Injector INSTANCE =
                Guice.createInjector(
                        new PropertiesModule(),
                        new VertxModule(),
                        new MailModule()
                );
    }

    public static Injector getInstance() {
        return LAZY_HOLDER.INSTANCE;
    }
}
