package com.lawrence.greenmail;

import com.google.inject.Injector;
import com.lawrence.greenmail.guice.injector.MainInjector;
import com.lawrence.greenmail.verticle.ReportVerticle;

import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Injector injector = MainInjector.getInstance();
        Vertx vertx = injector.getInstance(Vertx.class);
        ReportVerticle reportVerticle = injector.getInstance(ReportVerticle.class);

        vertx.deployVerticle(reportVerticle);
    }
}
