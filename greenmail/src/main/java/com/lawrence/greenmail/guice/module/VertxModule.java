package com.lawrence.greenmail.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.vertx.core.Vertx;

public class VertxModule extends AbstractModule {
    @Provides
    @Singleton
    Vertx provideVertx() {
        return Vertx.vertx();
    }
}
