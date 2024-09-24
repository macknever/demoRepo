package com.lawrence.guice.module;

import com.google.inject.AbstractModule;
import com.lawrence.notification.MessageFormatter;
import com.lawrence.notification.MessageFormatterImpl;
import com.lawrence.notification.MessageTemplateLoader;
import com.lawrence.notification.MessageTemplateLoaderImpl;

public class NotificationConfigureModule extends AbstractModule {

    @Override
    public void configure() {
        bind(MessageTemplateLoader.class).to(MessageTemplateLoaderImpl.class);
        bind(MessageFormatter.class).to(MessageFormatterImpl.class);
    }
}
