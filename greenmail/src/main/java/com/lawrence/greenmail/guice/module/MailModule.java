package com.lawrence.greenmail.guice.module;

import static com.lawrence.greenmail.guice.Constants.SMTP_SERVER_HOST_PROP;
import static com.lawrence.greenmail.guice.Constants.SMTP_SERVER_PASSWORD_PROP;
import static com.lawrence.greenmail.guice.Constants.SMTP_SERVER_PORT_PROP;
import static com.lawrence.greenmail.guice.Constants.SMTP_SERVER_USERNAME_PROP;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;

public class MailModule extends AbstractModule {

    public static final String SMTP_MAIL_CONFIG = "SMTP_MAIL_CONFIG";
    public static final String MAIL_CLIENT = "MAIL_CLIENT";

    @Provides
    @Named(SMTP_MAIL_CONFIG)
    MailConfig providesMailConfig(@Named(SMTP_SERVER_HOST_PROP) String host, @Named(SMTP_SERVER_PORT_PROP) int port,
            @Named(SMTP_SERVER_USERNAME_PROP) String userName, @Named(SMTP_SERVER_PASSWORD_PROP) String pwd) {
        return new MailConfig()
                .setAllowRcptErrors(true)
                .setHostname(host)
                .setPort(port)
                .setUsername(userName)
                .setPassword(pwd);
    }

    @Provides
    @Named(MAIL_CLIENT)
    MailClient providesMailClient(@Named(SMTP_MAIL_CONFIG) MailConfig config, Vertx vertx) {
        return MailClient.createShared(vertx, config);
    }
}
