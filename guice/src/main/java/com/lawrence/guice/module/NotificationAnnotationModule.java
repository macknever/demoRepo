package com.lawrence.guice.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.lawrence.notification.MessageFormatter;
import com.lawrence.notification.MessageFormatterAnnotatedImpl;
import com.lawrence.notification.MessageTemplateLoader;
import com.lawrence.notification.MessageTemplateLoaderAnnotatedImpl;

import jakarta.inject.Qualifier;

public class NotificationAnnotationModule extends AbstractModule {

    @Qualifier
    @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Loader {
    }

    @Qualifier
    @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Formatter {
    }

    @Provides
    @Loader
    MessageTemplateLoader provideMessageTemplateLoader(MessageTemplateLoaderAnnotatedImpl messageTemplateLoaderImpl) {
        return messageTemplateLoaderImpl;
    }

    @Override
    public void configure() {
        bind(MessageFormatter.class)
                .annotatedWith(Formatter.class)
                .to(MessageFormatterAnnotatedImpl.class);
    }

}
