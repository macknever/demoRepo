package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.lawrence.notification.MessageFormatter;
import com.lawrence.notification.MessageFormatterImpl;
import com.lawrence.notification.MessageTemplateLoader;
import com.lawrence.notification.MessageTemplateLoaderImpl;
import com.lawrence.notification.NotificationService;
import com.lawrence.notification.ValidationPrinter;

public class NotificationModule extends AbstractModule {

    @Provides
    MessageFormatter provideMessageFormatter(MessageFormatterImpl messageFormatterImpl) {
        return messageFormatterImpl;
    }

    @Provides
    MessageTemplateLoader provideMessageTemplateLoader(MessageTemplateLoaderImpl messageTemplateLoaderImpl) {
        return messageTemplateLoaderImpl;
    }

    @Override
    public void configure() {
        bind(String.class)
                .annotatedWith(Names.named("templateId"))
                .toInstance("TemplateId from guice");

        bind(String.class)
                .annotatedWith(Names.named("messageData"))
                .toInstance("messageData from guice");

        bind(NotificationService.class).toProvider(NotificationServiceProvider.class);

        bind(ValidationPrinter.class);
    }

}
